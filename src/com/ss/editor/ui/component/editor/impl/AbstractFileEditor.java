package com.ss.editor.ui.component.editor.impl;

import static java.util.Objects.requireNonNull;
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
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.FileChangedEvent;
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
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;

/**
 * The base implementation of an editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileEditor<R extends Pane> implements FileEditor {

    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(FileEditor.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
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

    protected AbstractFileEditor() {
        this.showedTime = LocalTime.now();
        this.editorStates = ArrayFactory.newArray(EditorAppState.class);
        this.dirtyProperty = new SimpleBooleanProperty(this, "dirty", false);
        this.fileChangedHandler = event -> processChangedFile((FileChangedEvent) event);
        createContent();
    }

    /**
     * Add the new 3D part of this editor.
     */
    protected void addEditorState(@NotNull final EditorAppState editorAppState) {
        this.editorStates.add(editorAppState);
    }

    /**
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
            toolbar.setId(CSSIds.FILE_EDITOR_TOOLBAR);
            toolbar.prefWidthProperty().bind(container.widthProperty());

            createToolbar(toolbar);

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
     */
    @FXThread
    protected void processKeyPressed(@NotNull final KeyEvent event) {
    }

    /**
     * Create toolbar.
     */
    protected void createToolbar(@NotNull final HBox container) {
    }

    /**
     * Create the save action.
     */
    @NotNull
    protected Button createSaveAction() {

        Button action = new Button();
        action.setTooltip(new Tooltip(Messages.FILE_EDITOR_ACTION_SAVE + " (Ctrl + S)"));
        action.setOnAction(event -> processSave());
        action.setGraphic(new ImageView(Icons.SAVE_16));
        action.disableProperty().bind(dirtyProperty().not());

        FXUtils.addClassTo(action, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(action, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

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
     * @return true if this editor needs a toolbar.
     */
    protected boolean needToolbar() {
        return false;
    }

    /**
     * @return the new root.
     */
    @NotNull
    protected abstract R createRoot();

    /**
     * Create content.
     */
    protected abstract void createContent(@NotNull final R root);

    @NotNull
    @Override
    public Pane getPage() {
        final R pane = requireNonNull(root);
        return (Pane) pane.getParent().getParent();
    }

    @NotNull
    @Override
    public Path getEditFile() {
        return requireNonNull(file);
    }

    @NotNull
    @Override
    public String getFileName() {
        final Path editFile = getEditFile();
        final Path fileName = editFile.getFileName();
        return fileName.toString();
    }

    @Override
    public void openFile(@NotNull final Path file) {
        FX_EVENT_MANAGER.addEventHandler(FileChangedEvent.EVENT_TYPE, getFileChangedHandler());

        this.file = file;
        this.showedTime = LocalTime.now();

        final EditorDescription description = getDescription();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_OPENED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendPageView(null, null, "/editing/" + description.getEditorId());
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
     */
    @FXThread
    public void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                    final float vRotation, final float targetDistance) {
    }

    @Override
    public void notifyShowed() {
        this.showedTime = LocalTime.now();

        final EditorDescription description = getDescription();
        GAnalytics.sendPageView(null, null, "/editing/" + description.getEditorId());
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
     */
    protected void processChangedFile(@NotNull final FileChangedEvent event) {
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
     * @param buttonLeftDown the left button is pressed.
     */
    protected void setButtonLeftDown(final boolean buttonLeftDown) {
        this.buttonLeftDown = buttonLeftDown;
    }

    /**
     * @param buttonMiddleDown the middle button is pressed.
     */
    protected void setButtonMiddleDown(final boolean buttonMiddleDown) {
        this.buttonMiddleDown = buttonMiddleDown;
    }

    /**
     * @param buttonRightDown the right button is pressed.
     */
    protected void setButtonRightDown(final boolean buttonRightDown) {
        this.buttonRightDown = buttonRightDown;
    }

    /**
     * @return true if left button is pressed.
     */
    protected boolean isButtonLeftDown() {
        return buttonLeftDown;
    }

    /**
     * @return true if middle button is pressed.
     */
    protected boolean isButtonMiddleDown() {
        return buttonMiddleDown;
    }

    /**
     * @return true if right button is pressed.
     */
    protected boolean isButtonRightDown() {
        return buttonRightDown;
    }

}
