package com.ss.editor.ui.component.editor.area;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.app.state.AppStateManager;
import com.jme3x.jfx.injfx.processor.FrameTransferSceneProcessor;
import com.ss.editor.JmeApplication;
import com.ss.editor.JfxApplication;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.file.converter.FileConverter;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.state.editor.Editor3DState;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.concurrent.util.ThreadUtils;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.DictionaryUtils;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * The component for containing editors.
 *
 * @author JavaSaBr
 */
public class EditorAreaComponent extends TabPane implements ScreenComponent {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(EditorAreaComponent.class);

    /**
     * The constant COMPONENT_ID.
     */
    @NotNull
    private static final String COMPONENT_ID = "EditorAreaComponent";

    /**
     * The constant KEY_EDITOR.
     */
    @NotNull
    private static final String KEY_EDITOR = "editor";

    @NotNull
    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();

    @NotNull
    private static final FileCreatorRegistry CREATOR_REGISTRY = FileCreatorRegistry.getInstance();

    @NotNull
    private static final WorkspaceManager WORKSPACE_MANAGER = WorkspaceManager.getInstance();

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    @NotNull
    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();

    @NotNull
    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    @NotNull
    private static final JfxApplication JFX_APPLICATION = JfxApplication.getInstance();

    @NotNull
    private static final JmeApplication JME_APPLICATION = JmeApplication.getInstance();

    /**
     * The table of opened editors.
     */
    @NotNull
    private final ConcurrentObjectDictionary<Path, Tab> openedEditors;

    /**
     * The flag for ignoring changing the list of opened editors.
     */
    private boolean ignoreOpenedFiles;

    /**
     * Instantiates a new Editor area component.
     */
    public EditorAreaComponent() {
        this.openedEditors = DictionaryFactory.newConcurrentAtomicObjectDictionary();

        setPickOnBounds(true);
        setId(CSSIds.EDITOR_AREA_COMPONENT);
        getTabs().addListener(this::processChangeTabs);
        getSelectionModel().selectedItemProperty()
                .addListener(this::switchEditor);

        FX_EVENT_MANAGER.addEventHandler(RequestedOpenFileEvent.EVENT_TYPE, event -> processOpenFile((RequestedOpenFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedCreateFileEvent.EVENT_TYPE, event -> processCreateFile((RequestedCreateFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedConvertFileEvent.EVENT_TYPE, event -> processConvertFile((RequestedConvertFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RenamedFileEvent.EVENT_TYPE, event -> processEvent((RenamedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(MovedFileEvent.EVENT_TYPE, event -> processEvent((MovedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processEvent((ChangedCurrentAssetFolderEvent) event));
    }

    @FxThread
    private void switchEditor(@NotNull final ObservableValue<? extends Tab> observable, @Nullable final Tab oldValue,
                              @Nullable final Tab newValue) {

        BorderPane current3DArea = null;
        BorderPane new3DArea = null;

        Path newCurrentFile = null;

        if (newValue != null) {

            final ObservableMap<Object, Object> properties = newValue.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            fileEditor.notifyShowed();

            newCurrentFile = fileEditor.getEditFile();
            new3DArea = fileEditor.get3DArea();
        }

        if (oldValue != null) {
            final ObservableMap<Object, Object> properties = oldValue.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            fileEditor.notifyHided();
            current3DArea = fileEditor.get3DArea();
        }

        final EditorFXScene scene = (EditorFXScene) getScene();
        final ImageView canvas = scene.getCanvas();

        if (new3DArea != null) {
            new3DArea.setCenter(canvas);
        } else if (current3DArea != null) {
            current3DArea.setCenter(null);
            scene.hideCanvas();
        }

        final Workspace workspace = notNull(WORKSPACE_MANAGER.getCurrentWorkspace());
        workspace.updateCurrentEditedFile(newCurrentFile);

        EXECUTOR_MANAGER.addJmeTask(() -> processShowEditor(oldValue, newValue));
    }

    /**
     * Handle changing the current asset folder.
     */
    @FxThread
    private void processEvent(@NotNull final ChangedCurrentAssetFolderEvent event) {
        setIgnoreOpenedFiles(true);
        try {

            final ObservableList<Tab> tabs = getTabs();
            tabs.clear();

            loadOpenedFiles();

        } finally {
            setIgnoreOpenedFiles(false);
        }
    }

    /**
     * @param ignoreOpenedFiles the flag for ignoring changing the list of opened editors.
     */
    @FromAnyThread
    private void setIgnoreOpenedFiles(final boolean ignoreOpenedFiles) {
        this.ignoreOpenedFiles = ignoreOpenedFiles;
    }

    /**
     * @return the flag for ignoring changing the list of opened editors.
     */
    @FromAnyThread
    private boolean isIgnoreOpenedFiles() {
        return ignoreOpenedFiles;
    }

    /**
     * Handle a renamed file.
     */
    @FxThread
    private void processEvent(@NotNull final RenamedFileEvent event) {

        final Path prevFile = event.getPrevFile();
        final Path newFile = event.getNewFile();

        handleMovedFiles(prevFile, newFile);
    }

    /**
     * Handle a moved file.
     */
    @FxThread
    private void processEvent(@NotNull final MovedFileEvent event) {

        final Path prevFile = event.getPrevFile();
        final Path newFile = event.getNewFile();

        handleMovedFiles(prevFile, newFile);
    }

    /**
     * Handle a moved/renamed file.
     *
     * @param prevFile the prev version of the file.
     * @param newFile  the new version of the file.
     */
    @FxThread
    private void handleMovedFiles(@NotNull final Path prevFile, @NotNull final Path newFile) {

        final ConcurrentObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
        final long stamp = openedEditors.writeLock();
        try {

            final Array<Path> files = openedEditors.keyArray(Path.class);
            for (final Path file : files) {

                if (!file.startsWith(prevFile)) {
                    continue;
                }

                final Tab tab = openedEditors.get(file);
                final ObservableMap<Object, Object> properties = tab.getProperties();
                final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
                fileEditor.notifyRenamed(prevFile, newFile);

                if (fileEditor.isDirty()) {
                    tab.setText("*" + fileEditor.getFileName());
                } else {
                    tab.setText(fileEditor.getFileName());
                }

                final Path editFile = fileEditor.getEditFile();

                openedEditors.remove(file);
                openedEditors.put(editFile, tab);

                final Workspace workspace = WORKSPACE_MANAGER.getCurrentWorkspace();

                if (workspace != null) {
                    workspace.removeOpenedFile(file);
                    workspace.addOpenedFile(editFile, fileEditor);
                }
            }

        } finally {
            openedEditors.writeUnlock(stamp);
        }
    }

    /**
     * Handle the request to convert a file.
     */
    @FxThread
    private void processConvertFile(@NotNull final RequestedConvertFileEvent event) {

        final Path file = event.getFile();
        final FileConverterDescription description = event.getDescription();

        final FileConverter converter = FILE_CONVERTER_REGISTRY.newCreator(description, file);
        if (converter == null) return;

        converter.convert(file);
    }

    /**
     * @return the tale of opened editors.
     */
    @NotNull
    @FromAnyThread
    private ConcurrentObjectDictionary<Path, Tab> getOpenedEditors() {
        return openedEditors;
    }

    /**
     * Handle the request to create a file.
     */
    @FxThread
    private void processCreateFile(@NotNull final RequestedCreateFileEvent event) {

        final Path file = event.getFile();
        final FileCreatorDescription description = event.getDescription();

        final FileCreator fileCreator = CREATOR_REGISTRY.newCreator(description, file);
        if (fileCreator == null) return;

        fileCreator.start(file);
    }

    /**
     * Handle the request to close a file editor.
     */
    @FxThread
    private void processChangeTabs(@NotNull final ListChangeListener.Change<? extends Tab> change) {
        if (!change.next()) return;

        final List<? extends Tab> removed = change.getRemoved();
        if (removed == null || removed.isEmpty()) return;

        removed.forEach(tab -> {

            final ObservableMap<Object, Object> properties = tab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            final Path editFile = fileEditor.getEditFile();

            DictionaryUtils.runInWriteLock(getOpenedEditors(), editFile, ObjectDictionary::remove);

            fileEditor.notifyClosed();

            if (isIgnoreOpenedFiles()) return;

            final Workspace workspace = WORKSPACE_MANAGER.getCurrentWorkspace();
            if (workspace != null) workspace.removeOpenedFile(editFile);
        });
    }

    /**
     * Get the current showed editor.
     *
     * @return the current editor.
     */
    @FxThread
    public @Nullable FileEditor getCurrentEditor() {
        final Tab selectedTab = getSelectionModel().getSelectedItem();
        if (selectedTab == null) return null;
        final ObservableMap<Object, Object> properties = selectedTab.getProperties();
        return (FileEditor) properties.get(KEY_EDITOR);
    }

    /**
     * Handle a changed active file editor.
     *
     * @param prevTab the previous editor.
     * @param newTab  the new editor.
     */
    @JmeThread
    private void processShowEditor(@Nullable final Tab prevTab, @Nullable final Tab newTab) {

        final AppStateManager stateManager = JME_APPLICATION.getStateManager();
        final ImageView canvas = JFX_APPLICATION.getScene().getCanvas();
        final FrameTransferSceneProcessor sceneProcessor = JFX_APPLICATION.getSceneProcessor();

        boolean enabled = false;

        if (prevTab != null) {

            final ObservableMap<Object, Object> properties = prevTab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);

            final Array<Editor3DState> states = fileEditor.get3DStates();
            states.forEach(stateManager::detach);
        }

        if (newTab != null) {

            final ObservableMap<Object, Object> properties = newTab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);

            final Array<Editor3DState> states = fileEditor.get3DStates();
            states.forEach(stateManager::attach);

            enabled = states.size() > 0;
        }

        if (sceneProcessor.isEnabled() != enabled) {
            final boolean result = enabled;
            EXECUTOR_MANAGER.addFxTask(() -> {
                ThreadUtils.sleep(100);
                canvas.setOpacity(result ? 1D : 0D);
                sceneProcessor.setEnabled(result);
            });
        }
    }

    /**
     * Handle the request to open a file.
     */
    @FxThread
    private void processOpenFile(@NotNull final RequestedOpenFileEvent event) {

        final Path file = event.getFile();

        final ConcurrentObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
        final Tab tab = DictionaryUtils.getInReadLock(openedEditors, file, ObjectDictionary::get);

        if (tab != null) {
            final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
            selectionModel.select(tab);
            return;
        }

        EditorUtil.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> processOpenFileImpl(event, file));
    }

    @BackgroundThread
    private void processOpenFileImpl(@NotNull final RequestedOpenFileEvent event, @NotNull final Path file) {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        FileEditor editor;
        try {

            final EditorDescription description = event.getDescription();

            editor = description == null ? EDITOR_REGISTRY.createEditorFor(file) :
                    EDITOR_REGISTRY.createEditorFor(description, file);

        } catch (final Throwable e) {
            EditorUtil.handleException(null, this, new Exception(e));
            EXECUTOR_MANAGER.addFxTask(scene::decrementLoading);
            return;
        }

        if (editor == null) {
            EXECUTOR_MANAGER.addFxTask(scene::decrementLoading);
            return;
        }

        final FileEditor resultEditor = editor;

        final long stamp = JME_APPLICATION.asyncLock();
        try {
            editor.openFile(file);
        } catch (final Throwable e) {
            EditorUtil.handleException(null, this, new Exception(e));

            final Workspace workspace = WORKSPACE_MANAGER.getCurrentWorkspace();
            if (workspace != null) {
                workspace.removeOpenedFile(file);
            }

            EXECUTOR_MANAGER.addFxTask(() -> {
                EditorUtil.decrementLoading();
                resultEditor.notifyClosed();
            });

            return;
        } finally {
            JME_APPLICATION.asyncUnlock(stamp);
        }

        EXECUTOR_MANAGER.addFxTask(() -> addEditor(resultEditor, event.isNeedShow()));
    }

    /**
     * Add and open the new file editor.
     *
     * @param editor   the editor
     * @param needShow the need show
     */
    @FxThread
    private void addEditor(@NotNull final FileEditor editor, final boolean needShow) {

        final Path editFile = editor.getEditFile();

        final Tab tab = new Tab(editor.getFileName());
        tab.setGraphic(new ImageView(ICON_MANAGER.getIcon(editFile, DEFAULT_FILE_ICON_SIZE)));
        tab.setContent(editor.getPage());
        tab.setOnCloseRequest(event -> handleRequestToCloseEditor(editor, tab, event));

        final ObservableMap<Object, Object> properties = tab.getProperties();
        properties.put(KEY_EDITOR, editor);

        editor.dirtyProperty().addListener((observable, oldValue, newValue) -> {
            tab.setText(newValue == Boolean.TRUE ? "*" + editor.getFileName() : editor.getFileName());
        });

        final ObservableList<Tab> tabs = getTabs();
        tabs.add(tab);

        if (needShow) {
            final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
            selectionModel.select(tab);
        }

        DictionaryUtils.runInWriteLock(getOpenedEditors(), editFile, tab, ObjectDictionary::put);

        EditorUtil.decrementLoading();

        if (isIgnoreOpenedFiles()) {
            return;
        }

        final Workspace workspace = WORKSPACE_MANAGER.getCurrentWorkspace();

        if (workspace != null) {
            workspace.addOpenedFile(editFile, editor);
        }
    }

    @FxThread
    private void handleRequestToCloseEditor(@NotNull final FileEditor editor, @NotNull final Tab tab,
                                            @NotNull final Event event) {
        if (!editor.isDirty()) {
            return;
        }

        final String question = Messages.EDITOR_AREA_SAVE_FILE_QUESTION.replace("%file_name%", editor.getFileName());

        final ConfirmDialog dialog = new ConfirmDialog(result -> {
            if (result == null) return;

            if (result) {
                editor.save(fileEditor -> getTabs().remove(tab));
            } else {
                getTabs().remove(tab);
            }

        }, question);

        dialog.show();
        event.consume();
    }

    @Override
    @FromAnyThread
    public @Nullable String getComponentId() {
        return COMPONENT_ID;
    }

    @Override
    @FxThread
    public void notifyFinishBuild() {
        setIgnoreOpenedFiles(true);
        try {
            loadOpenedFiles();
        } finally {
            setIgnoreOpenedFiles(false);
        }
    }

    /**
     * Load all opened files.
     */
    @FxThread
    private void loadOpenedFiles() {

        final Workspace workspace = WORKSPACE_MANAGER.getCurrentWorkspace();
        if (workspace == null) return;

        final Path assetFolder = workspace.getAssetFolder();
        final String editFile = workspace.getCurrentEditedFile();

        final Map<String, String> openedFiles = workspace.getOpenedFiles();
        openedFiles.forEach((assetPath, editorId) -> {

            final EditorDescription description = EDITOR_REGISTRY.getDescription(editorId);
            if (description == null) return;

            final Path file = assetFolder.resolve(assetPath);
            if (!Files.exists(file)) return;

            final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
            event.setFile(file);
            event.setDescription(description);
            event.setNeedShow(StringUtils.equals(assetPath, editFile));

            processOpenFile(event);
        });
    }
}
