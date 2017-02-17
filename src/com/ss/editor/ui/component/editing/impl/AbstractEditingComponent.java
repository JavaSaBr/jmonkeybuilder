package com.ss.editor.ui.component.editing.impl;

import static java.util.Objects.requireNonNull;
import static rlib.util.ClassUtils.unsafeCast;
import com.jme3.scene.Node;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.Editing3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editing.EditingComponent;
import com.ss.editor.ui.component.editing.EditingContainer;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of an editing component.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditingComponent<T> extends VBox implements EditingComponent {

    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The parent container.
     */
    @Nullable
    protected EditingContainer editingContainer;

    /**
     * The edited object.
     */
    @Nullable
    protected T editedObject;

    /**
     * Is showed this component.
     */
    protected boolean showed;

    public AbstractEditingComponent() {
        createComponents();
    }

    @Override
    public void initFor(@NotNull final EditingContainer container) {
        this.editingContainer = container;
        prefWidthProperty().bind(widthProperty());
    }

    /**
     * @return the parent container.
     */
    @NotNull
    public EditingContainer getEditingContainer() {
        return requireNonNull(editingContainer);
    }

    /**
     * @return the change consumer.
     */
    @NotNull
    public ModelChangeConsumer getChangeConsumer() {
        final EditingContainer editingContainer = getEditingContainer();
        return editingContainer.getChangeConsumer();
    }

    /**
     * @return the edited object.
     */
    @NotNull
    public T getEditedObject() {
        return requireNonNull(editedObject);
    }

    @Override
    public void startEditing(@NotNull final Object object) {
        this.editedObject = unsafeCast(object);
    }

    protected void createComponents() {
    }

    /**
     * Get a cursor node.
     *
     * @return the cursor node.
     */
    @NotNull
    protected Node getCursorNode() {
        final EditingContainer editingContainer = getEditingContainer();
        final Editing3DProvider editingProvider = editingContainer.getEditingProvider();
        return editingProvider.getCursorNode();
    }

    @Override
    public void notifyShowed() {
        setShowed(true);
    }

    @Override
    public void notifyHided() {
        setShowed(false);
    }

    /**
     * @return true if this component is showed.
     */
    protected boolean isShowed() {
        return showed;
    }

    /**
     * @param showed true if this component is showed.
     */
    protected void setShowed(final boolean showed) {
        this.showed = showed;
    }
}
