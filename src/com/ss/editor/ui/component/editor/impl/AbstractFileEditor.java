package com.ss.editor.ui.component.editor.impl;

import com.jme3.math.Vector3f;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.FileChangedEvent;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The base implementation of an editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileEditor<R extends Pane> implements FileEditor {

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
    private R root;

    /**
     * Rge edit file.
     */
    private Path file;

    public AbstractFileEditor() {
        this.showedTime = LocalTime.now();
        this.editorStates = ArrayFactory.newArray(EditorAppState.class);
        this.dirtyProperty = new SimpleBooleanProperty(this, "dirty", false);
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
     * Handle the key released event.
     */
    protected void processKeyReleased(@NotNull final KeyEvent event) {

        final KeyCode code = event.getCode();

        if (code == KeyCode.S && event.isControlDown() && isDirty()) {
            processSave();
        }
    }

    /**
     * Handle the key pressed event.
     */
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
        action.setGraphic(new ImageView(Icons.SAVE_16));
        action.setOnAction(event -> processSave());
        action.disableProperty().bind(dirtyProperty().not());

        FXUtils.addClassTo(action, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(action, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        return action;
    }

    /**
     * The process of saving this file.
     */
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
        return (Pane) root.getParent().getParent();
    }

    @NotNull
    @Override
    public Path getEditFile() {
        return file;
    }

    @NotNull
    @Override
    public String getFileName() {
        return file.getFileName().toString();
    }

    @Override
    public void openFile(@NotNull final Path file) {
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

    /**
     * Notify about changing the file.
     */
    protected void notifyFileChanged() {

        final FileChangedEvent event = new FileChangedEvent();
        event.setFile(getEditFile());

        FX_EVENT_MANAGER.notify(event);
    }

    @Override
    public void notifyRenamed(@NotNull final Path prevFile, @NotNull final Path newFile) {

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

    @Override
    public void notifyMoved(@NotNull Path prevFile, @NotNull Path newFile) {

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

        final Duration duration = Duration.between(showedTime, LocalTime.now());
        final int seconds = (int) duration.getSeconds();

        final EditorDescription description = getDescription();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_CLOSED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendTiming(GAEvent.Category.EDITOR, GAEvent.Label.WORKING_ON_AN_EDITOR,
                seconds, description.getEditorId());
    }

    @Override
    public String toString() {
        return "AbstractFileEditor{" +
                "dirtyProperty=" + dirtyProperty.get() +
                ", file=" + file +
                '}';
    }
}
