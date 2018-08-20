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
import com.ss.editor.ui.component.editor.event.ClosedFileEditorEvent;
import com.ss.editor.ui.component.editor.event.FileRenamedFileEditorEvent;
import com.ss.editor.ui.component.editor.event.HideFileEditorEvent;
import com.ss.editor.ui.component.editor.event.ShowedFileEditorEvent;
import com.ss.editor.ui.css.CssIds;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.concurrent.util.ThreadUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.ObservableUtils;
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
    private static final String KEY_EDITOR = "jMB.node.editor";

    /**
     * Require a file editor's reference from the tab.
     *
     * @param tab the tab.
     * @return the file editor.
     */
    @FxThread
    private static @NotNull FileEditor requireFileEditor(@NotNull Tab tab) {
        return (FileEditor) notNull(tab.getProperties()
                .get(KEY_EDITOR));
    }

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
        setPickOnBounds(true);
        setId(CssIds.EDITOR_AREA_COMPONENT);

        this.openedEditors = DictionaryFactory.newConcurrentAtomicObjectDictionary();
        this.openingFiles = ArrayFactory.newConcurrentStampedLockArray(Path.class);

        getTabs().addListener(this::processChangeTabs);

        FxControlUtils.onSelectedTabChange(this, this::switchEditor);

        FxEventManager.getInstance()
                .addEventHandler(RequestedOpenFileEvent.EVENT_TYPE, this::processOpenFile)
                .addEventHandler(RequestedCreateFileEvent.EVENT_TYPE, this::processCreateFile)
                .addEventHandler(RequestedConvertFileEvent.EVENT_TYPE, this::processConvertFile)
                .addEventHandler(RenamedFileEvent.EVENT_TYPE, this::processEvent)
                .addEventHandler(MovedFileEvent.EVENT_TYPE, this::processEvent)
                .addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, this::processEvent);
    }

    @FxThread
    private void switchEditor(@Nullable Tab oldValue, @Nullable Tab newValue) {

        BorderPane current3dArea = null;
        BorderPane new3dArea = null;

        Path newCurrentFile = null;

        if (newValue != null) {

            var fileEditor = requireFileEditor(newValue);
            fileEditor.notify(ShowedFileEditorEvent.getInstance());

            newCurrentFile = fileEditor.getFile();
            new3dArea = fileEditor.get3dArea();
        }

        if (oldValue != null) {

            var fileEditor = requireFileEditor(oldValue);
            fileEditor.notify(HideFileEditorEvent.getInstance());

            current3dArea = fileEditor.get3dArea();
        }

        var scene = (EditorFxScene) getScene();
        var canvas = scene.getCanvas();

        if (new3dArea != null) {
            new3dArea.setCenter(canvas);
        } else if (current3dArea != null) {
            current3dArea.setCenter(null);
            scene.hideCanvas();
        }

        WorkspaceManager.getInstance()
                .requiredCurrentWorkspace()
                .updateCurrentEditedFile(newCurrentFile);

        ExecutorManager.getInstance()
                .addJmeTask(() -> processShowEditor(oldValue, newValue));
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
    private void processEvent(@NotNull MovedFileEvent event) {
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

        var stamp = openedEditors.writeLock();
        try {

            var files = openedEditors.keyArray(Path.class);

            for (var file : files) {

                if (!file.startsWith(prevFile)) {
                    continue;
                }

                var tab = openedEditors.get(file);

                if (tab == null) {
                    LOGGER.warning("Not found a tab for the opened file " + file);
                    continue;
                }

                var fileEditor = requireFileEditor(tab);
                fileEditor.notify(new FileRenamedFileEditorEvent(prevFile, newFile));

                if (fileEditor.isDirty()) {
                    tab.setText("*" + fileEditor.getFileName());
                } else {
                    tab.setText(fileEditor.getFileName());
                }

                var editFile = fileEditor.getFile();

                openedEditors.remove(file);
                openedEditors.put(editFile, tab);

                WorkspaceManager.getInstance()
                        .getCurrentWorkspaceOpt()
                        .stream()
                        .peek(workspace -> workspace.removeOpenedFile(file))
                        .forEach(workspace -> workspace.addOpenedFile(editFile, fileEditor));
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

        FileConverterRegistry.getInstance()
                .newCreator(event.getDescription(), event.getFile())
                .convert(event.getFile());
    }

    /**
     * Handle the request to create a file.
     */
    @FxThread
    private void processCreateFile(@NotNull RequestedCreateFileEvent event) {

        FileCreatorRegistry.getInstance()
                .newCreatorOpt(event.getDescriptor(), event.getFile())
                .ifPresent(fileCreator -> fileCreator.start(event.getFile()));
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

            var fileEditor = requireFileEditor(tab);
            var editFile = fileEditor.getFile();

            openedEditors.runInWriteLock(editFile, ObjectDictionary::remove);

            fileEditor.notify(ClosedFileEditorEvent.getInstance());

            if (isIgnoreOpenedFiles()) {
                return;
            }

            WorkspaceManager.getInstance()
                    .getCurrentWorkspaceOpt()
                    .ifPresent(workspace -> workspace.removeOpenedFile(editFile));
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

        var stateManager = EditorUtils.getStateManager();
        var canvas = EditorUtils.getFxScene()
                .getCanvas();

        var sceneProcessor = JfxApplication.getInstance()
                .getSceneProcessor();

        boolean enabled = false;

        if (prevTab != null) {
            var parts = requireFileEditor(prevTab).get3dParts();
            parts.forEach(stateManager::detach);
        }

        if (newTab != null) {

            var parts = requireFileEditor(newTab).get3dParts();
            parts.forEach(stateManager::attach);

            enabled = parts.size() > 0;
        }

        if (sceneProcessor.isEnabled() != enabled) {
            var result = enabled;
            var executorManager = ExecutorManager.getInstance();
            executorManager.addFxTask(() -> {
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
        var tab = openedEditors.getInReadLock(file, ObjectDictionary::get);

        if (tab != null) {
            getSelectionModel().select(tab);
            return;
        }

        var stamp = openingFiles.writeLock();
        try {

            if (openingFiles.contains(file)) {
                return;
            }

            openingFiles.add(file);

            UiUtils.incrementLoading();

            ExecutorManager.getInstance()
                    .addBackgroundTask(() -> processOpenFileInBackground(event, file));

        } finally {
            openingFiles.writeUnlock(stamp);
        }
    }

    @BackgroundThread
    private void processOpenFileInBackground(@NotNull RequestedOpenFileEvent event, @NotNull Path file) {

        FileEditor editor;
        try {

            var description = event.getDescription();
            var editorRegistry = EditorRegistry.getInstance();

            editor = description == null ? editorRegistry.createEditorFor(file) :
                    editorRegistry.createEditorFor(description, file);

        } catch (Throwable e) {
            EditorUtils.handleException(null, this, new Exception(e));
            UiUtils.decrementLoading();
            openingFiles.runInWriteLock(file, Array::fastRemove);
            return;
        }

        if (editor == null) {
            UiUtils.decrementLoading();
            openingFiles.runInWriteLock(file, Array::fastRemove);
            return;
        }

        var resultEditor = editor;

        var jmeApplication = JmeApplication.getInstance();
        var stamp = jmeApplication.asyncLock();
        try {
            editor.openFile(file);
        } catch (Throwable e) {
            EditorUtils.handleException(null, this, new Exception(e));

            openingFiles.runInWriteLock(file, Array::fastRemove);

            WorkspaceManager.getInstance()
                    .requiredCurrentWorkspace()
                    .removeOpenedFile(file);

            UiUtils.decrementLoading();

            ExecutorManager.getInstance()
                    .addFxTask(() -> resultEditor.notify(ClosedFileEditorEvent.getInstance()));

            return;

        } finally {
            jmeApplication.asyncUnlock(stamp);
        }

        ExecutorManager.getInstance()
                .addFxTask(() -> addEditor(resultEditor, event.isNeedShow()));
    }

    /**
     * Add and open the new file editor.
     *
     * @param editor   the editor
     * @param needShow the need show
     */
    @FxThread
    private void addEditor(@NotNull FileEditor editor, boolean needShow) {

        var editFile = editor.getFile();
        var iconManager = FileIconManager.getInstance();

        var tab = new Tab(editor.getFileName());
        tab.setGraphic(new ImageView(iconManager.getIcon(editFile, DEFAULT_FILE_ICON_SIZE)));
        tab.setContent(editor.getUiPage());
        tab.setOnCloseRequest(event -> handleRequestToCloseEditor(editor, tab, event));
        tab.getProperties().put(KEY_EDITOR, editor);

        ObservableUtils.onChanges(editor.dirtyProperty())
                .onChangeIf(dirty -> dirty, () -> tab.setText("*" + editor.getFileName()))
                .onChangeIf(dirty -> !dirty, () -> tab.setText(editor.getFileName()));

        getTabs().add(tab);

        if (needShow) {
            getSelectionModel().select(tab);
        }

        openedEditors.runInWriteLock(editFile, tab, ObjectDictionary::put);
        openingFiles.runInWriteLock(editFile, Array::fastRemove);

        UiUtils.decrementLoading();

        if (isIgnoreOpenedFiles()) {
            return;
        }

        WorkspaceManager.getInstance()
                .getCurrentWorkspaceOpt()
                .ifPresent(workspace -> workspace.addOpenedFile(editFile, editor));
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
                //FIXME editor.save(fileEditor -> getTabs().remove(tab));
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
        ExecutorManager.getInstance()
                .addFxTask(this::notifyFinishBuildInFx);
    }

    @FxThread
    private void notifyFinishBuildInFx() {
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

        var workspace = WorkspaceManager.getInstance()
                .getCurrentWorkspace();

        if (workspace == null) {
            return;
        }

        var assetFolder = workspace.getAssetFolder();
        var editFile = workspace.getCurrentEditedFile();

        var openedFiles = workspace.getOpenedFiles();
        openedFiles.forEach((assetPath, editorId) -> {

            var description = EditorRegistry.getInstance()
                    .getDescription(editorId);

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
