package com.ss.editor.ui.control.model.tree.node;

import com.ss.editor.Editor;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;

import org.jetbrains.annotations.NotNull;

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
    public T getElement() {
        return element;
    }

    /**
     * @return the name of this node.
     */
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
    public Array<ModelNode<?>> getChildren() {
        return EMPTY_ARRAY;
    }

    /**
     * @return the icon of this node.
     */
    public Image getIcon() {
        return null;
    }

    /**
     * Fill the items actions for this node.
     */
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {
    }

    /**
     * Remove the child from this node.
     */
    public void remove(final ModelNode<?> child) {
    }

    /**
     * Add the new child to this node.
     */
    public void add(final ModelNode<?> child) {
    }

    /**
     * Handle changing the name of this node.
     */
    public void changeName(final ModelNodeTree nodeTree, final String newName) {
    }

    /**
     * @return true of this node can have the child.
     */
    public boolean canAccept(final ModelNode<?> child) {
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
    public ModelNode<?> copy() {
        return null;
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
