package com.ss.editor.ui.control.model.tree.node;

import com.ss.editor.Editor;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The base implementation of a node in the {@link ModelNodeTree}.
 *
 * @author JavaSaBr
 */
public abstract class ModelNode<T> implements UObject {

    private static final Array<ModelNode<?>> EMPTY_ARRAY = ArrayFactory.newArray(ModelNode.class);

    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The uniq id of this node.
     */
    private final long objectId;

    /**
     * The element of the {@link com.jme3.scene.Spatial}.
     */
    private final T element;

    public ModelNode(@NotNull final T element, final long objectId) {
        this.element = element;
        this.objectId = objectId;
    }

    /**
     * @return the element of the {@link com.jme3.scene.Spatial}.
     */
    @NotNull
    public T getElement() {
        return element;
    }

    /**
     * @return the name of this node.
     */
    @NotNull
    public String getName() {
        return "null";
    }

    /**
     * @return true of this node has any children.
     */
    public boolean hasChildren() {
        return false;
    }

    /**
     * @return the array of children of this node.
     */
    @NotNull
    public Array<ModelNode<?>> getChildren() {
        return EMPTY_ARRAY;
    }

    /**
     * @return the icon of this node.
     */
    @Nullable
    public Image getIcon() {
        return null;
    }

    /**
     * Fill the items actions for this node.
     */
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {
    }

    /**
     * Remove the child from this node.
     */
    public void remove(@NotNull final ModelNode<?> child) {
    }

    /**
     * Add the new child to this node.
     */
    public void add(@NotNull final ModelNode<?> child) {
    }

    /**
     * Handle changing the name of this node.
     */
    public void changeName(@NotNull final ModelNodeTree nodeTree, @NotNull final String newName) {
    }

    /**
     * @return true of this node can have the child.
     */
    public boolean canAccept(@NotNull final ModelNode<?> child) {
        return false;
    }

    /**
     * @return true if this node supports moving.
     */
    public boolean canMove() {
        return true;
    }

    /**
     * @return true if this node supports copying.
     */
    public boolean canCopy() {
        return false;
    }

    /**
     * @return true if this node supports name editing.
     */
    public boolean canEditName() {
        return false;
    }

    /**
     * @return the new copy of this node.
     */
    @NotNull
    public ModelNode<?> copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "objectId=" + objectId +
                ", element=" + element +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelNode<?> modelNode = (ModelNode<?>) o;
        return element.equals(modelNode.element);
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }
}
