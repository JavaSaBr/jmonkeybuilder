package com.ss.editor.ui.component.editor.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
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

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;

/**
 * The base implementation of an editor.
 *
 * @param <R> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractFileEditor<R extends Pane> implements FileEditor {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(FileEditor.class);

    /**
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The constant FX_EVENT_MANAGER.
     */
    @NotNull
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The constant JFX_APPLICATION.
     */
    @NotNull
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The array of 3D parts of this editor.
     */
    @NotNull
    private final Array<EditorAppState> editorStates;

    /**
     * The file changes listener.
     */
    @NotNull
    private final EventHandler<Event> fileChangedHandler;

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
     * The root element of this editor.
     */
    @Nullable
    private R root;

    /**
     * The edited file.
     */
    @Nullable
    private Path file;

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
    private boolean saving;

    /**
     * Instantiates a new Abstract file editor.
     */
    protected AbstractFileEditor() {
        this.showedTime = LocalTime.now();
        this.editorStates = ArrayFactory.newArray(EditorAppState.class);
        this.dirtyProperty = new SimpleBooleanProperty(this, "dirty", false);
        this.fileChangedHandler = event -> processChangedFile((FileChangedEvent) event);
        createContent();
    }

    /**
     * Add the new 3D part of this editor.
     *
     * @param editorAppState the editor app state
     */
    protected void addEditorState(@NotNull final EditorAppState editorAppState) {
        this.editorStates.add(editorAppState);
    }

    /**
     * Sets edit file.
     *
     * @param file the edit file.
     */
    protected void setEditFile(@NotNull final Path file) {
        this.file = file;
    }

    /**
     * Create content of this editor.
     */
    protected void createContent() {

        final VBox container = new VBox();
        final StackPane page = new StackPane(container);
        page.setPickOnBounds(true);

        HBox toolbar = null;

        if (needToolbar()) {

            toolbar = new HBox();
            toolbar.prefWidthProperty().bind(container.widthProperty());

            createToolbar(toolbar);

            FXUtils.addClassTo(toolbar, CSSClasses.FILE_EDITOR_TOOLBAR);
            FXUtils.addToPane(toolbar, container);
        }

        root = createRoot();
        root.setOnKeyPressed(this::processKeyPressed);
        root.setOnKeyReleased(this::processKeyReleased);
        root.setOnMouseReleased(this::processMouseReleased);
        root.setOnMousePressed(this::processMousePressed);

        createContent(root);

        FXUtils.addToPane(root, container);

        if (toolbar != null) {
            root.prefHeightProperty().bind(container.heightProperty().subtract(toolbar.heightProperty()));
        } else {
            root.prefHeightProperty().bind(container.heightProperty());
        }

        root.prefWidthProperty().bind(container.widthProperty());
    }

    /**
     * Handle the mouse released event.
     */
    private void processMouseReleased(@NotNull final MouseEvent mouseEvent) {
        setButtonLeftDown(mouseEvent.isPrimaryButtonDown());
        setButtonMiddleDown(mouseEvent.isMiddleButtonDown());
        setButtonRightDown(mouseEvent.isSecondaryButtonDown());
    }

    /**
     * Handle the mouse pressed event.
     */
    private void processMousePressed(@NotNull final MouseEvent mouseEvent) {
        setButtonLeftDown(mouseEvent.isPrimaryButtonDown());
        setButtonMiddleDown(mouseEvent.isMiddleButtonDown());
        setButtonRightDown(mouseEvent.isSecondaryButtonDown());
    }

    /**
     * Handle the key released event.
     *
     * @param event the event
     */
    protected void processKeyReleased(@NotNull final KeyEvent event) {

        final KeyCode code = event.getCode();

        if (code == KeyCode.S && event.isControlDown() && isDirty()) {
            processSave();
        }
    }

    /**
     * Handle a key code.
     *
     * @param keyCode            the key code.
     * @param isPressed          true if key is pressed.
     * @param isControlDown      true if control is down.
     * @param isButtonMiddleDown true if mouse middle button is pressed.
     */
    @FromAnyThread
    public void handleKeyAction(@NotNull final KeyCode keyCode, final boolean isPressed, final boolean isControlDown,
                                final boolean isButtonMiddleDown) {
        EXECUTOR_MANAGER.addFXTask(() -> handleKeyActionImpl(keyCode, isPressed, isControlDown, isButtonMiddleDown));
    }

    /**
     * Handle a key code.
     *
     * @param keyCode            the key code.
     * @param isPressed          true if key is pressed.
     * @param isControlDown      true if control is down.
     * @param isButtonMiddleDown true if mouse middle button is pressed.
     * @return true if can consume an event.
     */
    @FXThread
    protected boolean handleKeyActionImpl(@NotNull final KeyCode keyCode, final boolean isPressed,
                                          final boolean isControlDown, final boolean isButtonMiddleDown) {
        return false;
    }

    /**
     * Handle the key pressed event.
     *
     * @param event the event
     */
    @FXThread
    protected void processKeyPressed(@NotNull final KeyEvent event) {
    }

    /**
     * Create toolbar.
     *
     * @param container the container
     */
    protected void createToolbar(@NotNull final HBox container) {
    }

    /**
     * Create the save action.
     *
     * @return the button
     */
    @NotNull
    protected Button createSaveAction() {

        final Button action = new Button();
        action.setTooltip(new Tooltip(Messages.FILE_EDITOR_ACTION_SAVE + " (Ctrl + S)"));
        action.setOnAction(event -> processSave());
        action.setGraphic(new ImageView(Icons.SAVE_16));
        action.disableProperty().bind(dirtyProperty().not());

        FXUtils.addClassesTo(action, CSSClasses.FLAT_BUTTON,
                CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(action);

        return action;
    }

    /**
     * The process of saving this file.
     */
    @FXThread
    protected void processSave() {
        final long stamp = EDITOR.asyncLock();
        try {
            doSave();
        } finally {
            EDITOR.asyncUnlock(stamp);
        }
    }

    /**
     * Need toolbar boolean.
     *
     * @return true if this editor needs a toolbar.
     */
    protected boolean needToolbar() {
        return false;
    }

    /**
     * Create root r.
     *
     * @return the new root.
     */
    @NotNull
    protected abstract R createRoot();

    /**
     * Create content.
     *
     * @param root the root
     */
    protected abstract void createContent(@NotNull final R root);

    @NotNull
    @Override
    public Pane getPage() {
        final R pane = notNull(root);
        return (Pane) pane.getParent().getParent();
    }

    @NotNull
    @Override
    public Path getEditFile() {
        return notNull(file);
    }

    @NotNull
    @Override
    public String getFileName() {
        final Path editFile = getEditFile();
        final Path fileName = editFile.getFileName();
        return fileName.toString();
    }

    @FXThread
    @Override
    public void openFile(@NotNull final Path file) {
        FX_EVENT_MANAGER.addEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());

        this.file = file;
        this.showedTime = LocalTime.now();

        final EditorDescription description = getDescription();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_OPENED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendPageView(description.getEditorId(), null, "/editing/" + description.getEditorId());
    }

    @NotNull
    @Override
    public BooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

    @Override
    public boolean isDirty() {
        return dirtyProperty.get();
    }

    /**
     * Set the flag of dirty of this editor.
     *
     * @param dirty the dirty
     */
    protected void setDirty(final boolean dirty) {
        this.dirtyProperty.setValue(dirty);
    }

    @NotNull
    @Override
    public Array<EditorAppState> getStates() {
        return editorStates;
    }

    @Override
    public void notifyRenamed(@NotNull final Path prevFile, @NotNull final Path newFile) {
        notifyChangedEditedFile(prevFile, newFile);
    }

    @Override
    public void notifyMoved(@NotNull final Path prevFile, final @NotNull Path newFile) {
        notifyChangedEditedFile(prevFile, newFile);
    }

    /**
     * Notify about changed the edited file.
     *
     * @param prevFile the prev file.
     * @param newFile  the new file.
     */
    @FXThread
    private void notifyChangedEditedFile(final @NotNull Path prevFile, final @NotNull Path newFile) {

        final Path editFile = getEditFile();

        if (editFile.equals(prevFile)) {
            setEditFile(newFile);
            return;
        }

        if (!editFile.startsWith(prevFile)) return;

        final Path relativeFile = editFile.subpath(prevFile.getNameCount(), editFile.getNameCount());
        final Path resultFile = newFile.resolve(relativeFile);

        setEditFile(resultFile);
    }

    /**
     * Notify about changing editor camera.
     *
     * @param cameraLocation the camera location
     * @param hRotation      the h rotation
     * @param vRotation      the v rotation
     * @param targetDistance the target distance
     */
    @FXThread
    public void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                    final float vRotation, final float targetDistance) {
    }

    @Override
    public void notifyShowed() {
        this.showedTime = LocalTime.now();

        final EditorDescription description = getDescription();
        GAnalytics.sendPageView(description.getEditorId(), null, "/editing/" + description.getEditorId());
    }

    @Override
    public void notifyHided() {

        final Duration duration = Duration.between(showedTime, LocalTime.now());
        final int seconds = (int) duration.getSeconds();

        final EditorDescription description = getDescription();

        GAnalytics.sendTiming(GAEvent.Category.EDITOR, GAEvent.Label.WORKING_ON_AN_EDITOR,
                seconds, description.getEditorId());
    }

    @Override
    public void notifyClosed() {
        FX_EVENT_MANAGER.removeEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());

        final Duration duration = Duration.between(showedTime, LocalTime.now());
        final int seconds = (int) duration.getSeconds();

        final EditorDescription description = getDescription();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_CLOSED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendTiming(GAEvent.Category.EDITOR, GAEvent.Label.WORKING_ON_AN_EDITOR,
                seconds, description.getEditorId());
    }

    /**
     * Handle a changed file.
     *
     * @param event the event
     */
    protected void processChangedFile(@NotNull final FileChangedEvent event) {

        final Path file = event.getFile();
        final Path editFile = getEditFile();

        if (!file.equals(editFile)) {
            return;
        }

        if (isSaving()) {
            notifyFinishSaving();
            return;
        }

        handleExternalChanges();
    }

    /**
     * Handle external changes of the edited file.
     */
    protected void handleExternalChanges() {

    }

    /**
     * @return the file changes listener.
     */
    @NotNull
    private EventHandler<Event> getFileChangedHandler() {
        return fileChangedHandler;
    }

    @Override
    public String toString() {
        return "AbstractFileEditor{" +
                "dirtyProperty=" + dirtyProperty.get() +
                ", file=" + file +
                '}';
    }

    /**
     * Sets button left down.
     *
     * @param buttonLeftDown the left button is pressed.
     */
    protected void setButtonLeftDown(final boolean buttonLeftDown) {
        this.buttonLeftDown = buttonLeftDown;
    }

    /**
     * Sets button middle down.
     *
     * @param buttonMiddleDown the middle button is pressed.
     */
    protected void setButtonMiddleDown(final boolean buttonMiddleDown) {
        this.buttonMiddleDown = buttonMiddleDown;
    }

    /**
     * Sets button right down.
     *
     * @param buttonRightDown the right button is pressed.
     */
    protected void setButtonRightDown(final boolean buttonRightDown) {
        this.buttonRightDown = buttonRightDown;
    }

    /**
     * Is button left down boolean.
     *
     * @return true if left button is pressed.
     */
    protected boolean isButtonLeftDown() {
        return buttonLeftDown;
    }

    /**
     * Is button middle down boolean.
     *
     * @return true if middle button is pressed.
     */
    protected boolean isButtonMiddleDown() {
        return buttonMiddleDown;
    }

    /**
     * Is button right down boolean.
     *
     * @return true if right button is pressed.
     */
    protected boolean isButtonRightDown() {
        return buttonRightDown;
    }

    /**
     * Is saving boolean.
     *
     * @return the boolean
     */
    protected boolean isSaving() {
        return saving;
    }

    /**
     * Sets saving.
     *
     * @param saving the saving
     */
    protected void setSaving(final boolean saving) {
        this.saving = saving;
    }

    @Override
    public void doSave() {
        notifyStartSaving();
    }

    /**
     * Notify start saving.
     */
    protected void notifyStartSaving() {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.incrementLoading();
        setSaving(true);
    }

    /**
     * Notify finish saving.
     */
    protected void notifyFinishSaving() {
        setSaving(false);
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.decrementLoading();
    }
}
