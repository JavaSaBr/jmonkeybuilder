package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.FileChangedEvent;

import java.nio.file.Path;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.ui.css.CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON;
import static com.ss.editor.ui.css.CSSClasses.TOOLBAR_BUTTON;
import static com.ss.editor.ui.css.CSSIds.FILE_EDITOR_TOOLBAR;

/**
 * Базовая реализация редактора.
 *
 * @author Ronn
 */
public abstract class AbstractFileEditor<R extends Pane> implements FileEditor {

    protected static final Logger LOGGER = LoggerManager.getLogger(FileEditor.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Список 3D частей этого редактора.
     */
    private final Array<EditorState> editorStates;

    /**
     * Изменялся ли документ.
     */
    private final BooleanProperty dirtyProperty;

    /**
     * Корневой элемент редактора.
     */
    private R root;

    /**
     * Редактируемый файл.
     */
    private Path file;

    public AbstractFileEditor() {
        this.editorStates = ArrayFactory.newArray(EditorState.class);
        this.dirtyProperty = new SimpleBooleanProperty(this, "dirty", false);
        createContent();
    }

    /**
     * Зарегистрировать 3D часть редактора.
     */
    protected void addEditorState(final EditorState editorState) {
        this.editorStates.add(editorState);
    }

    /**
     * Создание контента.
     */
    protected void createContent() {

        final VBox container = new VBox();

        HBox toolbar = null;

        if (needToolbar()) {

            toolbar = new HBox();
            toolbar.setId(FILE_EDITOR_TOOLBAR);

            createToolbar(toolbar);

            FXUtils.addToPane(toolbar, container);
            FXUtils.bindFixedWidth(toolbar, container.widthProperty());
        }

        root = createRoot();

        createContent(root);

        FXUtils.addToPane(root, container);

        if (toolbar != null) {
            FXUtils.bindFixedHeight(root, container.heightProperty().subtract(toolbar.heightProperty()));
        } else {
            FXUtils.bindFixedHeight(root, container.heightProperty());
        }

        FXUtils.bindFixedWidth(root, container.widthProperty());

        new StackPane(container);
    }

    /**
     * Создание тулбара.
     */
    protected void createToolbar(final HBox container) {
    }

    /**
     * Создание акшена для сохранения изменений.
     */
    protected Button createSaveAction() {

        Button action = new Button();
        action.setGraphic(new ImageView(Icons.SAVE_24));
        action.setOnAction(event -> doSave());
        action.disableProperty().bind(dirtyProperty().not());

        FXUtils.addClassTo(action, TOOLBAR_BUTTON);
        FXUtils.addClassTo(action, FILE_EDITOR_TOOLBAR_BUTTON);

        return action;
    }

    /**
     * @return нужен ли тулбар для этого редактора.
     */
    protected boolean needToolbar() {
        return false;
    }

    /**
     * @return создание корневого элемента для основного контента редактора.
     */
    protected abstract R createRoot();

    /**
     * Создание контента редактора.
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
     * Указать есть ли изменения в редакторе.
     */
    protected void setDirty(final boolean dirty) {
        this.dirtyProperty.setValue(dirty);
    }

    @Override
    public Array<EditorState> getStates() {
        return editorStates;
    }

    /**
     * Уведомление всех об изменении редактируемого файла.
     */
    protected void notifyFileChanged() {

        final FileChangedEvent event = new FileChangedEvent();
        event.setFile(getEditFile());

        FX_EVENT_MANAGER.notify(event);
    }

    @Override
    public String toString() {
        return "AbstractFileEditor{" +
                "editorStates=" + editorStates +
                ", dirtyProperty=" + dirtyProperty +
                ", file=" + file +
                '}';
    }
}
