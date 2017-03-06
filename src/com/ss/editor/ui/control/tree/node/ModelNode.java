package com.ss.editor.ui.control.tree.node;

import com.ss.editor.Editor;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The base implementation of a node.
 *
 * @author JavaSaBr
 */
public abstract class ModelNode<T> implements UObject {

    @NotNull
    protected static final Array<ModelNode<?>> EMPTY_ARRAY = ArrayFactory.newArray(ModelNode.class);

    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The uniq id of this node.
     */
    private final long objectId;

    /**
     * The element of the {@link com.jme3.scene.Spatial}.
     */
    private final T element;

    /**
     * The parent.
     */
    private ModelNode<?> parent;

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
        return "unknown name";
    }

    /**
     * @param name the name.
     */
    public void setName(@NotNull final String name) {
    }

    /**
     * @return true if need to save name.
     */
    public boolean isNeedToSaveName() {
        return false;
    }

    /**
     * @return true of this node has any children.
     */
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return false;
    }

    /**
     * @return the array of children of this node.
     */
    @NotNull
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return EMPTY_ARRAY;
    }

    /**
     * @return the parent of this node.
     */
    @Nullable
    public ModelNode<?> getParent() {
        return parent;
    }

    /**
     * @param parent the parent.
     */
    protected void setParent(final ModelNode<?> parent) {
        this.parent = parent;
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
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
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
    public void changeName(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final String newName) {
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
     * @return true if you can remove this node.
     */
    public boolean canRemove() {
        return true;
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

    /**
     * Notify about that a model node was added to children of this node.
     *
     * @param modelNode the model node.
     */
    public void notifyChildAdded(@NotNull final ModelNode<?> modelNode) {
    }

    /**
     * Notify about that a model node was removed from children of this node.
     *
     * @param modelNode the model node.
     */
    public void notifyChildRemoved(@NotNull final ModelNode<?> modelNode) {
        modelNode.setParent(null);
    }

    /**
     * Notify about that a model node will add to children of this node.
     *
     * @param modelNode the model node.
     */
    public void notifyChildPreAdd(@NotNull final ModelNode<?> modelNode) {
        modelNode.setParent(this);
    }

    /**
     * Notify about that a model node will remove from children of this node.
     *
     * @param modelNode the model node.
     */
    public void notifyChildPreRemove(@NotNull final ModelNode<?> modelNode) {
    }
}
