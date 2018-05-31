package com.ss.editor.ui.component.editor.area;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.JfxApplication;
import com.ss.editor.JmeApplication;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CssIds;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.concurrent.util.ThreadUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.DictionaryUtils;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The component for containing editors.
 *
 * @author JavaSaBr
 */
public class EditorAreaComponent extends TabPane implements ScreenComponent {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorAreaComponent.class);

    private static final String COMPONENT_ID = "EditorAreaComponent";
    private static final String KEY_EDITOR = "editor";

    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();
    private static final WorkspaceManager WORKSPACE_MANAGER = WorkspaceManager.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();
    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();
    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The table of opened editors.
     */
    @NotNull
    private final ConcurrentObjectDictionary<Path, Tab> openedEditors;

    /**
     * The list of opened files.
     */
    @NotNull
    private final ConcurrentArray<Path> openingFiles;

    /**
     * True if need to ignore change events.
     */
    private boolean ignoreOpenedFiles;

    public EditorAreaComponent() {
        this.openedEditors = DictionaryFactory.newConcurrentAtomicObjectDictionary();
        this.openingFiles = ArrayFactory.newConcurrentStampedLockArray(Path.class);

        setPickOnBounds(true);
        setId(CssIds.EDITOR_AREA_COMPONENT);
        getTabs().addListener(this::processChangeTabs);
        getSelectionModel().selectedItemProperty()
                .addListener(this::switchEditor);

        FX_EVENT_MANAGER.addEventHandler(RequestedOpenFileEvent.EVENT_TYPE,
                event -> processOpenFile((RequestedOpenFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedCreateFileEvent.EVENT_TYPE,
                event -> processCreateFile((RequestedCreateFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedConvertFileEvent.EVENT_TYPE,
                event -> processConvertFile((RequestedConvertFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RenamedFileEvent.EVENT_TYPE,
                event -> processEvent((RenamedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(MovedFileEvent.EVENT_TYPE,
                event -> processEvent((MovedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE,
                event -> processEvent((ChangedCurrentAssetFolderEvent) event));
    }

    @FxThread
    private void switchEditor(
            @NotNull ObservableValue<? extends Tab> observable,
            @Nullable Tab oldValue,
            @Nullable Tab newValue
    ) {

        BorderPane current3DArea = null;
        BorderPane new3DArea = null;

        Path newCurrentFile = null;

        if (newValue != null) {

            var properties = newValue.getProperties();
            var fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            fileEditor.notifyShowed();

            newCurrentFile = fileEditor.getEditFile();
            new3DArea = fileEditor.get3DArea();
        }

        if (oldValue != null) {

            var properties = oldValue.getProperties();
            var fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            fileEditor.notifyHided();

            current3DArea = fileEditor.get3DArea();
        }

        var scene = (EditorFxScene) getScene();
        var canvas = scene.getCanvas();

        if (new3DArea != null) {
            new3DArea.setCenter(canvas);
        } else if (current3DArea != null) {
            current3DArea.setCenter(null);
            scene.hideCanvas();
        }

        var workspace = notNull(WORKSPACE_MANAGER.getCurrentWorkspace());
        workspace.updateCurrentEditedFile(newCurrentFile);

        EXECUTOR_MANAGER.addJmeTask(() -> processShowEditor(oldValue, newValue));
    }

    /**
     * Handle the event of changing a current asset folder.
     */
    @FxThread
    private void processEvent(@NotNull ChangedCurrentAssetFolderEvent event) {
        setIgnoreOpenedFiles(true);
        try {
            getTabs().clear();
            loadOpenedFiles();
        } finally {
            setIgnoreOpenedFiles(false);
        }
    }

    /**
     * Set true if need to ignore change events.
     *
     * @param ignoreOpenedFiles true if need to ignore change events.
     */
    @FromAnyThread
    private void setIgnoreOpenedFiles(boolean ignoreOpenedFiles) {
        this.ignoreOpenedFiles = ignoreOpenedFiles;
    }

    /**
     * Return true if need to ignore change events.
     *
     * @return true if need to ignore change events.
     */
    @FromAnyThread
    private boolean isIgnoreOpenedFiles() {
        return ignoreOpenedFiles;
    }

    /**
     * Handle the event of renamed file.
     */
    @FxThread
    private void processEvent(@NotNull RenamedFileEvent event) {
        handleMovedFiles(event.getPrevFile(), event.getNewFile());
    }

    /**
     * Handle the event of moved file.
     */
    @FxThread
    private void processEvent(@NotNull final MovedFileEvent event) {
        handleMovedFiles(event.getPrevFile(), event.getNewFile());
    }

    /**
     * Handle a moved/renamed file.
     *
     * @param prevFile the prev version of the file.
     * @param newFile  the new version of the file.
     */
    @FxThread
    private void handleMovedFiles(@NotNull Path prevFile, @NotNull Path newFile) {

        var openedEditors = getOpenedEditors();
        var stamp = openedEditors.writeLock();
        try {

            var files = openedEditors.keyArray(Path.class);

            for (var file : files) {

                if (!file.startsWith(prevFile)) {
                    continue;
                }

                var tab = openedEditors.get(file);
                var properties = tab.getProperties();
                var fileEditor = (FileEditor) properties.get(KEY_EDITOR);
                fileEditor.notifyRenamed(prevFile, newFile);

                if (fileEditor.isDirty()) {
                    tab.setText("*" + fileEditor.getFileName());
                } else {
                    tab.setText(fileEditor.getFileName());
                }

                var editFile = fileEditor.getEditFile();

                openedEditors.remove(file);
                openedEditors.put(editFile, tab);

                var workspace = WORKSPACE_MANAGER.getCurrentWorkspace();

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
    private void processConvertFile(@NotNull RequestedConvertFileEvent event) {

        var file = event.getFile();
        var description = event.getDescription();
        var converter = FILE_CONVERTER_REGISTRY.newCreator(description, file);

        if (converter != null) {
            converter.convert(file);
        }
    }

    /**
     * Get the tale of opened editors.
     *
     * @return the tale of opened editors.
     */
    @FromAnyThread
    private @NotNull ConcurrentObjectDictionary<Path, Tab> getOpenedEditors() {
        return openedEditors;
    }

    /**
     * Get the list of opened files.
     *
     * @return the list of opened files.
     */
    @FromAnyThread
    private @NotNull ConcurrentArray<Path> getOpeningFiles() {
        return openingFiles;
    }

    /**
     * Handle the request to create a file.
     */
    @FxThread
    private void processCreateFile(@NotNull RequestedCreateFileEvent event) {

        var registry = FileCreatorRegistry.getInstance();
        var file = event.getFile();
        var description = event.getDescription();
        var fileCreator = registry.newCreator(description, file);

        if (fileCreator != null) {
            fileCreator.start(file);
        }
    }

    /**
     * Handle the request to close a file editor.
     */
    @FxThread
    private void processChangeTabs(@NotNull ListChangeListener.Change<? extends Tab> change) {

        if (!change.next()) {
            return;
        }

        var removed = change.getRemoved();
        if (removed == null || removed.isEmpty()) {
            return;
        }

        removed.forEach(tab -> {

            var properties = tab.getProperties();
            var fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            var editFile = fileEditor.getEditFile();

            DictionaryUtils.runInWriteLock(getOpenedEditors(), editFile, ObjectDictionary::remove);

            fileEditor.notifyClosed();

            if (isIgnoreOpenedFiles()) {
                return;
            }

            var workspace = WORKSPACE_MANAGER.getCurrentWorkspace();
            if (workspace != null) {
                workspace.removeOpenedFile(editFile);
            }
        });
    }

    /**
     * Get the current showed editor.
     *
     * @return the current editor.
     */
    @FxThread
    public @Nullable FileEditor getCurrentEditor() {

        var selectedTab = getSelectionModel()
                .getSelectedItem();

        if (selectedTab == null) {
            return null;
        }

        return (FileEditor) selectedTab.getProperties()
                .get(KEY_EDITOR);
    }

    /**
     * Handle a changed active file editor.
     *
     * @param prevTab the previous editor.
     * @param newTab  the new editor.
     */
    @JmeThread
    private void processShowEditor(@Nullable Tab prevTab, @Nullable Tab newTab) {

        var stateManager = EditorUtil.getStateManager();
        var canvas = EditorUtil.getFxScene().getCanvas();
        var sceneProcessor = JfxApplication.getInstance()
                .getSceneProcessor();

        boolean enabled = false;

        if (prevTab != null) {

            var fileEditor = (FileEditor) prevTab.getProperties()
                    .get(KEY_EDITOR);

            var states = fileEditor.get3DStates();
            states.forEach(stateManager::detach);
        }

        if (newTab != null) {

            var fileEditor = (FileEditor) newTab.getProperties()
                    .get(KEY_EDITOR);

            var states = fileEditor.get3DStates();
            states.forEach(stateManager::attach);

            enabled = states.size() > 0;
        }

        if (sceneProcessor.isEnabled() != enabled) {
            var result = enabled;
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
    private void processOpenFile(@NotNull RequestedOpenFileEvent event) {

        var file = event.getFile();

        var openedEditors = getOpenedEditors();
        var tab = DictionaryUtils.getInReadLock(openedEditors, file, ObjectDictionary::get);

        if (tab != null) {
            getSelectionModel().select(tab);
            return;
        }

        var openingFiles = getOpeningFiles();
        var stamp = openingFiles.writeLock();
        try {

            if (openingFiles.contains(file)) {
                return;
            }

            openingFiles.add(file);

            UiUtils.incrementLoading();

            EXECUTOR_MANAGER.addBackgroundTask(() -> processOpenFileImpl(event, file));

        } finally {
            openingFiles.writeUnlock(stamp);
        }
    }

    @BackgroundThread
    private void processOpenFileImpl(@NotNull RequestedOpenFileEvent event, @NotNull Path file) {

        var scene = EditorUtil.getFxScene();

        FileEditor editor;
        try {

            var description = event.getDescription();

            editor = description == null ? EDITOR_REGISTRY.createEditorFor(file) :
                    EDITOR_REGISTRY.createEditorFor(description, file);

        } catch (Throwable e) {
            EditorUtil.handleException(null, this, new Exception(e));
            EXECUTOR_MANAGER.addFxTask(scene::decrementLoading);
            ArrayUtils.runInWriteLock(getOpeningFiles(), file, Array::fastRemove);
            return;
        }

        if (editor == null) {
            EXECUTOR_MANAGER.addFxTask(scene::decrementLoading);
            ArrayUtils.runInWriteLock(getOpeningFiles(), file, Array::fastRemove);
            return;
        }

        var resultEditor = editor;

        var jmeApplication = JmeApplication.getInstance();
        var stamp = jmeApplication.asyncLock();
        try {
            editor.openFile(file);
        } catch (Throwable e) {
            EditorUtil.handleException(null, this, new Exception(e));
            ArrayUtils.runInWriteLock(getOpeningFiles(), file, Array::fastRemove);

            var workspace = WORKSPACE_MANAGER.getCurrentWorkspace();
            if (workspace != null) {
                workspace.removeOpenedFile(file);
            }

            EXECUTOR_MANAGER.addFxTask(() -> {
                UiUtils.decrementLoading();
                resultEditor.notifyClosed();
            });

            return;

        } finally {
            jmeApplication.asyncUnlock(stamp);
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
    private void addEditor(@NotNull FileEditor editor, boolean needShow) {

        var editFile = editor.getEditFile();

        var tab = new Tab(editor.getFileName());
        tab.setGraphic(new ImageView(ICON_MANAGER.getIcon(editFile, DEFAULT_FILE_ICON_SIZE)));
        tab.setContent(editor.getPage());
        tab.setOnCloseRequest(event -> handleRequestToCloseEditor(editor, tab, event));
        tab.getProperties().put(KEY_EDITOR, editor);

        editor.dirtyProperty().addListener((observable, oldValue, newValue) ->
                tab.setText(newValue == Boolean.TRUE ? "*" + editor.getFileName() : editor.getFileName()));

        getTabs().add(tab);

        if (needShow) {
            getSelectionModel().select(tab);
        }

        DictionaryUtils.runInWriteLock(getOpenedEditors(), editFile, tab, ObjectDictionary::put);
        ArrayUtils.runInWriteLock(getOpeningFiles(), editFile, Array::fastRemove);

        UiUtils.decrementLoading();

        if (isIgnoreOpenedFiles()) {
            return;
        }

        var workspace = WORKSPACE_MANAGER.getCurrentWorkspace();
        if (workspace != null) {
            workspace.addOpenedFile(editFile, editor);
        }
    }

    @FxThread
    private void handleRequestToCloseEditor(@NotNull FileEditor editor, @NotNull Tab tab, @NotNull Event event) {

        if (!editor.isDirty()) {
            return;
        }

        var question = Messages.EDITOR_AREA_SAVE_FILE_QUESTION
                .replace("%file_name%", editor.getFileName());

        var dialog = new ConfirmDialog(result -> {

            if (result == null) {
                return;
            }

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
    @BackgroundThread
    public void notifyFinishBuild() {
        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {
            setIgnoreOpenedFiles(true);
            try {
                loadOpenedFiles();
            } finally {
                setIgnoreOpenedFiles(false);
            }
        });
    }

    /**
     * Load all opened files.
     */
    @FxThread
    private void loadOpenedFiles() {

        var workspace = WORKSPACE_MANAGER.getCurrentWorkspace();

        if (workspace == null) {
            return;
        }

        var assetFolder = workspace.getAssetFolder();
        var editFile = workspace.getCurrentEditedFile();

        var openedFiles = workspace.getOpenedFiles();
        openedFiles.forEach((assetPath, editorId) -> {

            var description = EDITOR_REGISTRY.getDescription(editorId);
            if (description == null) {
                return;
            }

            var file = assetFolder.resolve(assetPath);
            if (!Files.exists(file)) {
                return;
            }

            var event = new RequestedOpenFileEvent(file);
            event.setDescription(description);
            event.setNeedShow(StringUtils.equals(assetPath, editFile));

            processOpenFile(event);
        });
    }
}
