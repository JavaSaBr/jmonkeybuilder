package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.FileChangedEvent;

import java.nio.file.Path;

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
    private final Array<EditorState> editorStates;

    /**
     * The dirty property.
     */
    private final BooleanProperty dirtyProperty;

    /**
     * The root element of this editor.
     */
    private R root;

    /**
     * Rge edit file.
     */
    private Path file;

    public AbstractFileEditor() {
        this.editorStates = ArrayFactory.newArray(EditorState.class);
        this.dirtyProperty = new SimpleBooleanProperty(this, "dirty", false);
        createContent();
    }

    /**
     * Add the new 3D part of this editor.
     */
    protected void addEditorState(final EditorState editorState) {
        this.editorStates.add(editorState);
    }

    /**
     * @param file the edit file.
     */
    protected void setEditFile(final Path file) {
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
    protected void processKeyReleased(final KeyEvent event) {

        final KeyCode code = event.getCode();

        if (code == KeyCode.S && event.isControlDown() && isDirty()) {
            processSave();
        }
    }

    /**
     * Handle the key pressed event.
     */
    protected void processKeyPressed(final KeyEvent event) {
    }

    /**
     * Create toolbar.
     */
    protected void createToolbar(final HBox container) {
    }

    /**
     * Create the save action.
     */
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
    protected abstract R createRoot();

    /**
     * Create content.
     */
    protected abstract void createContent(final R root);

    @Override
    public Pane getPage() {
        return (Pane) root.getParent().getParent();
    }

    @Override
    public Path getEditFile() {
        return file;
    }

    @Override
    public String getFileName() {
        return file.getFileName().toString();
    }

    @Override
    public void openFile(final Path file) {
        this.file = file;
    }

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

    @Override
    public Array<EditorState> getStates() {
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
    public void notifyRenamed(final Path prevFile, final Path newFile) {

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
    public void notifyMoved(Path prevFile, Path newFile) {

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
    public String toString() {
        return "AbstractFileEditor{" +
                "dirtyProperty=" + dirtyProperty.get() +
                ", file=" + file +
                '}';
    }
}
