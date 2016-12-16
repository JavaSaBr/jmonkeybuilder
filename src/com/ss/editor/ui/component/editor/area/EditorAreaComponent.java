package com.ss.editor.ui.component.editor.area;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;

import com.jme3.app.state.AppStateManager;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.file.converter.FileConverter;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.state.editor.EditorAppState;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.ui.event.impl.MovedFileEvent;
import com.ss.editor.ui.event.impl.RenamedFileEvent;
import com.ss.editor.ui.event.impl.RequestedConvertFileEvent;
import com.ss.editor.ui.event.impl.RequestedCreateFileEvent;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The component for containing editors.
 *
 * @author JavaSaBr
 */
public class EditorAreaComponent extends TabPane implements ScreenComponent {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorAreaComponent.class);

    public static final String COMPONENT_ID = "EditorAreaComponent";
    public static final String KEY_EDITOR = "editor";

    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();
    private static final FileCreatorRegistry CREATOR_REGISTRY = FileCreatorRegistry.getInstance();
    private static final WorkspaceManager WORKSPACE_MANAGER = WorkspaceManager.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final EditorRegistry EDITOR_REGISTRY = EditorRegistry.getInstance();
    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * The tale of opened editors.
     */
    private final ObjectDictionary<Path, Tab> openedEditors;

    /**
     * The flag for ignoring changing the list of opened editors.
     */
    private boolean ignoreOpenedFiles;

    public EditorAreaComponent() {
        setId(CSSIds.EDITOR_AREA_COMPONENT);
        setPickOnBounds(true);

        this.openedEditors = DictionaryFactory.newConcurrentAtomicObjectDictionary();

        final ObservableList<Tab> tabs = getTabs();
        tabs.addListener(this::processChangeTabs);

        final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            Path newCurrentFile = null;

            if (newValue != null) {

                final ObservableMap<Object, Object> properties = newValue.getProperties();
                final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
                fileEditor.notifyShowed();

                newCurrentFile = fileEditor.getEditFile();
            }

            if (oldValue != null) {
                final ObservableMap<Object, Object> properties = oldValue.getProperties();
                final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
                fileEditor.notifyHided();
            }

            final Workspace workspace = Objects.requireNonNull(WORKSPACE_MANAGER.getCurrentWorkspace(),
                    "The current workspace can't be null.");

            workspace.updateCurrentEditedFile(newCurrentFile);

            EXECUTOR_MANAGER.addEditorThreadTask(() -> processShowEditor(oldValue, newValue));
        });

        FX_EVENT_MANAGER.addEventHandler(RequestedOpenFileEvent.EVENT_TYPE, event -> processOpenFile((RequestedOpenFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedCreateFileEvent.EVENT_TYPE, event -> processCreateFile((RequestedCreateFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestedConvertFileEvent.EVENT_TYPE, event -> processConvertFile((RequestedConvertFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RenamedFileEvent.EVENT_TYPE, event -> processEvent((RenamedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(MovedFileEvent.EVENT_TYPE, event -> processEvent((MovedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processEvent((ChangedCurrentAssetFolderEvent) event));
    }

    /**
     * Handle changing the current asset folder.
     */
    private void processEvent(final ChangedCurrentAssetFolderEvent event) {
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
    private void setIgnoreOpenedFiles(final boolean ignoreOpenedFiles) {
        this.ignoreOpenedFiles = ignoreOpenedFiles;
    }

    /**
     * @return the flag for ignoring changing the list of opened editors.
     */
    private boolean isIgnoreOpenedFiles() {
        return ignoreOpenedFiles;
    }

    /**
     * Handle renaming a file.
     */
    private void processEvent(final RenamedFileEvent event) {

        final Path prevFile = event.getPrevFile();
        final Path newFile = event.getNewFile();

        final ObservableList<Tab> tabs = getTabs();
        tabs.forEach(tab -> {

            final ObservableMap<Object, Object> properties = tab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            fileEditor.notifyRenamed(prevFile, newFile);

            final Path editFile = fileEditor.getEditFile();

            if (!editFile.equals(newFile)) {
                return;
            }

            if (fileEditor.isDirty()) {
                tab.setText("*" + fileEditor.getFileName());
            } else {
                tab.setText(fileEditor.getFileName());
            }
        });
    }

    /**
     * Handle moving a file.
     */
    private void processEvent(final MovedFileEvent event) {

        final Path prevFile = event.getPrevFile();
        final Path newFile = event.getNewFile();

        final ObservableList<Tab> tabs = getTabs();
        tabs.forEach(tab -> {

            final ObservableMap<Object, Object> properties = tab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            fileEditor.notifyMoved(prevFile, newFile);
        });
    }

    /**
     * @return the tale of opened editors.
     */
    private ObjectDictionary<Path, Tab> getOpenedEditors() {
        return openedEditors;
    }

    /**
     * Handle the request for converting a file.
     */
    private void processConvertFile(final RequestedConvertFileEvent event) {

        final Path file = event.getFile();
        final FileConverterDescription description = event.getDescription();

        final FileConverter converter = FILE_CONVERTER_REGISTRY.newCreator(description, file);
        if (converter == null) return;

        converter.convert(file);
    }

    /**
     * Handle the request for creating a file.
     */
    private void processCreateFile(final RequestedCreateFileEvent event) {

        final Path file = event.getFile();
        final FileCreatorDescription description = event.getDescription();

        final FileCreator fileCreator = CREATOR_REGISTRY.newCreator(description, file);
        if (fileCreator == null) return;

        fileCreator.start(file);
    }

    /**
     * Handle the request for closing an Editor..
     */
    private void processChangeTabs(final ListChangeListener.Change<? extends Tab> change) {
        if (!change.next()) return;

        final List<? extends Tab> removed = change.getRemoved();
        if (removed == null || removed.isEmpty()) return;

        removed.forEach(tab -> {

            final ObservableMap<Object, Object> properties = tab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);
            final Path editFile = fileEditor.getEditFile();

            final ObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
            openedEditors.remove(editFile);

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
    public FileEditor getCurrentEditor() {
        final Tab selectedTab = getSelectionModel().getSelectedItem();
        if (selectedTab == null) return null;
        final ObservableMap<Object, Object> properties = selectedTab.getProperties();
        return (FileEditor) properties.get(KEY_EDITOR);
    }

    /**
     * Handle changing an active editor.
     *
     * @param prevTab the previous editor.
     * @param newTab  the new editor.
     */
    private void processShowEditor(final Tab prevTab, final Tab newTab) {

        final AppStateManager stateManager = EDITOR.getStateManager();

        if (prevTab != null) {

            final ObservableMap<Object, Object> properties = prevTab.getProperties();
            final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);

            final Array<EditorAppState> states = fileEditor.getStates();
            states.forEach(stateManager::detach);
        }

        if (newTab == null) return;

        final ObservableMap<Object, Object> properties = newTab.getProperties();
        final FileEditor fileEditor = (FileEditor) properties.get(KEY_EDITOR);

        final Array<EditorAppState> states = fileEditor.getStates();
        states.forEach(stateManager::attach);
    }

    /**
     * Handle the request for opening a file.
     */
    private void processOpenFile(final RequestedOpenFileEvent event) {

        final Path file = event.getFile();

        final ObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
        final Tab tab = openedEditors.get(file);

        if (tab != null) {
            final SingleSelectionModel<Tab> selectionModel = getSelectionModel();
            selectionModel.select(tab);
            return;
        }

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> processOpenFileImpl(event, file));
    }

    private void processOpenFileImpl(final RequestedOpenFileEvent event, final Path file) {

        final EditorDescription description = event.getDescription();
        final FileEditor editor = description == null ? EDITOR_REGISTRY.createEditorFor(file) : EDITOR_REGISTRY.createEditorFor(description, file);
        if (editor == null) return;

        final long stamp = EDITOR.asyncLock();
        try {
            editor.openFile(file);
        } catch (final Exception e) {
            EditorUtil.handleException(null, this, e);
            EXECUTOR_MANAGER.addFXTask(() -> {
                final EditorFXScene scene = JFX_APPLICATION.getScene();
                scene.decrementLoading();
            });
            return;
        } finally {
            EDITOR.asyncUnlock(stamp);
        }

        EXECUTOR_MANAGER.addFXTask(() -> addEditor(editor, event.isNeedShow()));
    }

    /**
     * Add and open new editor.
     */
    public void addEditor(final FileEditor editor, final boolean needShow) {

        final Path editFile = editor.getEditFile();

        final Tab tab = new Tab(editor.getFileName());
        tab.setGraphic(new ImageView(ICON_MANAGER.getIcon(editFile, DEFAULT_FILE_ICON_SIZE)));
        tab.setContent(editor.getPage());

        FXUtils.addClassTo(tab, CSSClasses.MAIN_FONT_12);

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

        final ObjectDictionary<Path, Tab> openedEditors = getOpenedEditors();
        openedEditors.put(editFile, tab);

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.decrementLoading();

        if (isIgnoreOpenedFiles()) {
            return;
        }

        final Workspace workspace = WORKSPACE_MANAGER.getCurrentWorkspace();

        if (workspace != null) {
            workspace.addOpenedFile(editFile, editor);
        }
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }

    @Override
    public void notifyFinishBuild() {
        setIgnoreOpenedFiles(true);
        try {
            loadOpenedFiles();
        } finally {
            setIgnoreOpenedFiles(false);
        }
    }

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
