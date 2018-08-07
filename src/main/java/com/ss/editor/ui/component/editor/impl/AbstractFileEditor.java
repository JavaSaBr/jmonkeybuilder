package com.ss.editor.ui.component.editor.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

/**
 * The base implementation of an editor.
 *
 * @param <R> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractFileEditor<R extends Pane> implements FileEditor {

    protected static final Logger LOGGER = LoggerManager.getLogger(FileEditor.class);

    /**
     * The array of editor's 3D parts.
     */
    @NotNull
    private final Array<Editor3dPart> editor3dParts;

    /**
     * The editedFile changes listener.
     */
    @NotNull
    private final EventHandler<FileChangedEvent> fileChangedHandler;

    /**
     * The dirty property.
     */
    @NotNull
    private final BooleanProperty dirtyProperty;

    /**
     * The time when this editor was showed.
     */
    @NotNull
    private volatile LocalTime showedTime;

    /**
     * The save callback.
     */
    @Nullable
    private CompletableFuture<FileEditor> saveCallback;

    /**
     * The root element of this editor.
     */
    @NotNull
    private final R root;

    /**
     * The edited editedFile.
     */
    @Nullable
    private volatile Path editedFile;

    /**
     * Is left button pressed.
     */
    private boolean buttonLeftDown;

    /**
     * Is right button pressed.
     */
    private boolean buttonRightDown;

    /**
     * Is middle button pressed.
     */
    private boolean buttonMiddleDown;

    /**
     * The flag of saving process.
     */
    private volatile boolean saving;

    protected AbstractFileEditor() {
        this.showedTime = LocalTime.now();
        this.editor3dParts = ArrayFactory.newArray(Editor3dPart.class);
        this.dirtyProperty = new SimpleBooleanProperty(this, "dirty", false);
        this.fileChangedHandler = this::processChangedFile;
        this.root = createRoot();
    }

    /**
     * Add the new editor's 3D part.
     *
     * @param editor3dPart the editor's 3D part.
     */
    @FxThread
    protected void addEditor3dPart(@NotNull Editor3dPart editor3dPart) {
        this.editor3dParts.add(editor3dPart);
    }

    /**
     * Set the edited file.
     *
     * @param editedFile the edited file.
     */
    @FxThread
    protected void setEditedFile(@NotNull Path editedFile) {
        this.editedFile = editedFile;
    }

    @Override
    @FxThread
    public void buildUi() {

        var container = new VBox();

        var page = new StackPane(container);
        page.setPickOnBounds(true);

        HBox toolbar = null;

        if (needToolbar()) {

            toolbar = new HBox();
            toolbar.prefWidthProperty()
                    .bind(container.widthProperty());

            createToolbar(toolbar);

            FxUtils.addClass(toolbar, CssClasses.FILE_EDITOR_TOOLBAR);
            FxUtils.addChild(container, toolbar);
        }


        if (needListenEventsFromPage()) {
            root.setOnKeyPressed(this::processKeyPressed);
            root.setOnKeyReleased(this::processKeyReleased);
            root.setOnMouseReleased(this::processMouseReleased);
            root.setOnMousePressed(this::processMousePressed);
        }

        createContent(root);

        FxUtils.addChild(container, root);

        if (toolbar != null) {
            root.prefHeightProperty()
                    .bind(container.heightProperty().subtract(toolbar.heightProperty()));
        } else {
            root.prefHeightProperty()
                    .bind(container.heightProperty());
        }

        root.prefWidthProperty()
                .bind(container.widthProperty());
    }

    /**
     * Return true if need to listen to events from root page of this editor.
     *
     * @return true if need to listen to events from root page of this editor.
     */
    protected boolean needListenEventsFromPage() {
        return true;
    }

    /**
     * Handle the mouse released event.
     */
    @FxThread
    private void processMouseReleased(@NotNull MouseEvent mouseEvent) {
        setButtonLeftDown(mouseEvent.isPrimaryButtonDown());
        setButtonMiddleDown(mouseEvent.isMiddleButtonDown());
        setButtonRightDown(mouseEvent.isSecondaryButtonDown());
    }

    /**
     * Handle the mouse pressed event.
     */
    @FxThread
    private void processMousePressed(@NotNull MouseEvent mouseEvent) {
        setButtonLeftDown(mouseEvent.isPrimaryButtonDown());
        setButtonMiddleDown(mouseEvent.isMiddleButtonDown());
        setButtonRightDown(mouseEvent.isSecondaryButtonDown());
    }

    /**
     * Handle the key released event.
     *
     * @param event the event
     */
    @FxThread
    protected void processKeyReleased(@NotNull KeyEvent event) {

        var code = event.getCode();

        if (handleKeyActionInFx(code, false, event.isControlDown(), event.isShiftDown(), false)) {
            event.consume();
        }
    }

    @Override
    @FromAnyThread
    public void handleKeyAction(
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    ) {
        ExecutorManager.getInstance()
                .addFxTask(() -> handleKeyActionInFx(keyCode, isPressed, isControlDown, isShiftDown, isButtonMiddleDown));
    }

    /**
     * Handle a key action in Fx thread.
     *
     * @param keyCode            the key code.
     * @param isPressed          true if key is pressed.
     * @param isControlDown      true if control is down.
     * @param isShiftDown        true if shift is down.
     * @param isButtonMiddleDown true if mouse middle button is pressed.
     * @return true if need to consume the event.
     */
    @FxThread
    protected boolean handleKeyActionInFx(
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    ) {
        return false;
    }

    /**
     * Handle the key pressed event.
     *
     * @param event the event.
     */
    @FxThread
    protected void processKeyPressed(@NotNull KeyEvent event) {

        var code = event.getCode();

        if (code == KeyCode.S && event.isControlDown() && isDirty()) {
            save();
        } else if (handleKeyActionInFx(code, true, event.isControlDown(), event.isShiftDown(), false)) {
            event.consume();
        }
    }

    /**
     * Create a toolbar.
     *
     * @param container the container for the toolbar.
     */
    @FromAnyThread
    protected void createToolbar(@NotNull HBox container) {
    }

    /**
     * Create the save action.
     *
     * @return the button
     */
    @FxThread
    protected @NotNull Button createSaveAction() {

        final Button action = new Button();
        action.setTooltip(new Tooltip(Messages.FILE_EDITOR_ACTION_SAVE + " (Ctrl + S)"));
        action.setOnAction(event -> save());
        action.setGraphic(new ImageView(Icons.SAVE_16));
        action.disableProperty().bind(dirtyProperty().not());

        FXUtils.addClassesTo(action, CssClasses.FLAT_BUTTON,
                CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(action);

        return action;
    }

    @Override
    @FromAnyThread
    public @NotNull CompletableFuture<FileEditor> save() {

        var result = new CompletableFuture<FileEditor>();

        ExecutorManager.getInstance()
                .addFxTask(() -> checkAndSaveIdNeed(result));

        return result;
    }

    /**
     * Check and if need to save the current state of this editor.
     *
     * @param callback the callback.
     */
    @FxThread
    protected void checkAndSaveIdNeed(@NotNull CompletableFuture<FileEditor> callback) {

        if (isSaving() || !isDirty()) {
            callback.complete(this);
            return;
        }

        this.saveCallback = callback;

        notifyStartSaving();

        ExecutorManager.getInstance()
                .addBackgroundTask(this::saveInBackground);
    }

    @BackgroundThread
    protected void saveInBackground() {

        var editorId = getDescriptor()
                .getEditorId();

        var tempFile = Utils.get(editorId,
                prefix -> Files.createTempFile(prefix, "toSave.tmp"));

        var stamp = EditorUtils.renderLock();
        try {

            doSave(tempFile);

            try (var out = Files.newOutputStream(getEditFile(), TRUNCATE_EXISTING)) {
                Files.copy(tempFile, out);
            } finally {
                FileUtils.delete(tempFile);
            }

        } catch (Throwable e) {
            EditorUtils.handleException(LOGGER, this, e);

            ExecutorManager.getInstance()
                    .addFxTask(this::notifyFinishSaving);

        } finally {
            EditorUtils.renderUnlock(stamp);
        }

        ExecutorManager.getInstance()
                .addFxTask(this::postSave);
    }

    /**
     * Save new changes.
     *
     * @param toStore the file to store.
     * @throws IOException if was some problem during writing.
     */
    @BackgroundThread
    protected void doSave(@NotNull Path toStore) throws Throwable {
    }

    /**
     * Do some actions after saving.
     */
    @FxThread
    protected void postSave() {
        setDirty(false);
    }

    /**
     * Return true if this editor needs a toolbar.
     *
     * @return true if this editor needs a toolbar.
     */
    @FromAnyThread
    protected boolean needToolbar() {
        return false;
    }

    /**
     * Create a root container.
     *
     * @return the new root container.
     */
    @FromAnyThread
    protected abstract @NotNull R createRoot();

    /**
     * Create editor's content.
     *
     * @param root the root container.
     */
    @FromAnyThread
    protected abstract void createContent(@NotNull R root);

    @Override
    @FxThread
    public @NotNull Pane getUiPage() {
        return (Pane) root.getParent()
                .getParent();
    }

    @Override
    @FxThread
    public @NotNull Path getEditFile() {
        return notNull(editedFile);
    }

    @Override
    @FxThread
    public @NotNull String getFileName() {
        return getEditFile().getFileName()
                .toString();
    }

    @Override
    @BackgroundThread
    public void openFile(@NotNull Path file) {

        FxEventManager.getInstance()
                .addEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());

        this.editedFile = file;
        this.showedTime = LocalTime.now();

        var description = getDescriptor();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_OPENED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendPageView(description.getEditorId(), null, "/editing/" + description.getEditorId());
    }

    @Override
    @FxThread
    public @NotNull BooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

    @Override
    @FromAnyThread
    public boolean isDirty() {
        synchronized (dirtyProperty) {
            return dirtyProperty.get();
        }
    }

    /**
     * Set the flag of dirty of this editor.
     *
     * @param dirty the dirty
     */
    @FromAnyThread
    protected void setDirty(boolean dirty) {
        synchronized (dirtyProperty) {
            dirtyProperty.setValue(dirty);
        }
    }

    @Override
    @FxThread
    public @NotNull Array<Editor3dPart> get3dParts() {
        return editor3dParts;
    }

    @Override
    @FxThread
    public void notifyRenamed(@NotNull Path prevFile, @NotNull Path newFile) {
        notifyChangedEditedFile(prevFile, newFile);
    }

    @Override
    @FxThread
    public void notifyMoved(@NotNull Path prevFile, @NotNull Path newFile) {
        notifyChangedEditedFile(prevFile, newFile);
    }

    /**
     * Notify about changed the edited file.
     *
     * @param prevFile the previous file.
     * @param newFile  the new file.
     */
    @FxThread
    private void notifyChangedEditedFile(@NotNull Path prevFile, @NotNull Path newFile) {

        var editFile = getEditFile();

        if (editFile.equals(prevFile)) {
            setEditedFile(newFile);
            return;
        }

        if (!editFile.startsWith(prevFile)) {
            return;
        }

        var relativeFile = editFile.subpath(prevFile.getNameCount(), editFile.getNameCount());
        var resultFile = newFile.resolve(relativeFile);

        setEditedFile(resultFile);
    }

    /**
     * Notify about changed editor camera settings.
     *
     * @param cameraLocation the camera location.
     * @param hRotation      the h rotation.
     * @param vRotation      the v rotation.
     * @param targetDistance the target distance.
     * @param cameraSpeed    the camera speed.
     */
    @FxThread
    public void notifyChangedCameraSettings(
            @NotNull Vector3f cameraLocation,
            float hRotation,
            float vRotation,
            float targetDistance,
            float cameraSpeed
    ) {
    }

    @Override
    @FxThread
    public void notifyShowed() {
        this.showedTime = LocalTime.now();

        var description = getDescriptor();

        GAnalytics.sendPageView(description.getEditorId(), null,
                "/editing/" + description.getEditorId());
    }

    @Override
    @FxThread
    public void notifyHided() {

        var duration = Duration.between(showedTime, LocalTime.now());
        var seconds = (int) duration.getSeconds();

        var description = getDescriptor();

        GAnalytics.sendTiming(GAEvent.Category.EDITOR, GAEvent.Label.WORKING_ON_AN_EDITOR,
                seconds, description.getEditorId());
    }

    @Override
    @FxThread
    public void notifyClosed() {

        FxEventManager.getInstance()
                .removeEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());

        var duration = Duration.between(showedTime, LocalTime.now());
        var seconds = (int) duration.getSeconds();

        var description = getDescriptor();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_CLOSED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendTiming(GAEvent.Category.EDITOR, GAEvent.Label.WORKING_ON_AN_EDITOR,
                seconds, description.getEditorId());
    }

    /**
     * Handle the event about some file was changed.
     *
     * @param event the file changed event.
     */
    @FxThread
    protected void processChangedFile(@NotNull FileChangedEvent event) {

        var file = event.getFile();
        var editFile = getEditFile();

        if (!file.equals(editFile)) {
            return;
        }

        processChangedFileImpl(event);

        if (isSaving()) {
            notifyFinishSaving();
            return;
        }

        handleExternalChanges();
    }

    /**
     * Handle the event about the edited file was changed.
     *
     * @param event the file changed event.
     */
    @FxThread
    protected void processChangedFileImpl(@NotNull FileChangedEvent event) {
    }

    /**
     * Handle external changes of the edited editedFile.
     */
    @FxThread
    protected void handleExternalChanges() {

    }

    /**
     * Get the file changes listener.
     *
     * @return the file changes listener.
     */
    @FxThread
    private @NotNull EventHandler<FileChangedEvent> getFileChangedHandler() {
        return fileChangedHandler;
    }

    /**
     * Set true if the left button is pressed.
     *
     * @param buttonLeftDown true if the left button is pressed.
     */
    @FxThread
    protected void setButtonLeftDown(boolean buttonLeftDown) {
        this.buttonLeftDown = buttonLeftDown;
    }

    /**
     * Set true if the middle button is pressed.
     *
     * @param buttonMiddleDown true if the middle button is pressed.
     */
    @FxThread
    protected void setButtonMiddleDown(boolean buttonMiddleDown) {
        this.buttonMiddleDown = buttonMiddleDown;
    }

    /**
     * Set true if the right button is pressed.
     *
     * @param buttonRightDown true if the right button is pressed.
     */
    @FxThread
    protected void setButtonRightDown(boolean buttonRightDown) {
        this.buttonRightDown = buttonRightDown;
    }

    /**
     * Return true if left button is pressed.
     *
     * @return true if left button is pressed.
     */
    @FxThread
    protected boolean isButtonLeftDown() {
        return buttonLeftDown;
    }

    /**
     * Return true if middle button is pressed.
     *
     * @return true if middle button is pressed.
     */
    @FxThread
    protected boolean isButtonMiddleDown() {
        return buttonMiddleDown;
    }

    /**
     * Return true if right button is pressed.
     *
     * @return true if right button is pressed.
     */
    @FxThread
    protected boolean isButtonRightDown() {
        return buttonRightDown;
    }

    /**
     * Return true if saving process is running now.
     *
     * @return true if saving process is running now.
     */
    @FxThread
    protected boolean isSaving() {
        return saving;
    }

    /**
     * Set true if saving process is running now.
     *
     * @param saving true if saving process is running now.
     */
    @FxThread
    protected void setSaving(boolean saving) {
        this.saving = saving;
    }

    /**
     * Notify start saving.
     */
    @FxThread
    protected void notifyStartSaving() {
        UiUtils.incrementLoading();
        setSaving(true);
    }

    /**
     * Notify finish saving.
     */
    @FxThread
    protected void notifyFinishSaving() {
        setSaving(false);

        UiUtils.decrementLoading();

        if (saveCallback != null) {
            saveCallback.complete(this);
            saveCallback = null;
        }
    }

    @Override
    public String toString() {
        return "AbstractFileEditor{" +
                "dirtyProperty=" + dirtyProperty.get() +
                ", editedFile=" + editedFile +
                '}';
    }

}
