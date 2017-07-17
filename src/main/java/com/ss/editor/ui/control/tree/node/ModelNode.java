package com.ss.editor.ui.control.tree.node;

import com.ss.editor.Editor;
import com.ss.editor.model.UObject;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The base implementation of a node.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class ModelNode<T> implements UObject {

    /**
     * The constant EMPTY_ARRAY.
     */
    @NotNull
    protected static final Array<ModelNode<?>> EMPTY_ARRAY = ArrayFactory.newArray(ModelNode.class);

    /**
     * The constant EDITOR.
     */
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

    /**
     * Instantiates a new Model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ModelNode(@NotNull final T element, final long objectId) {
        this.element = element;
        this.objectId = objectId;
    }

    /**
     * Gets element.
     *
     * @return the element of the {@link com.jme3.scene.Spatial}.
     */
    @NotNull
    public T getElement() {
        return element;
    }

    /**
     * Gets name.
     *
     * @return the name of this node.
     */
    @NotNull
    public String getName() {
        return "unknown name";
    }

    /**
     * Sets name.
     *
     * @param name the name.
     */
    public void setName(@NotNull final String name) {
    }

    /**
     * Is need to save name boolean.
     *
     * @return true if need to save name.
     */
    public boolean isNeedToSaveName() {
        return false;
    }

    /**
     * Has children boolean.
     *
     * @param nodeTree the node tree
     * @return true of this node has any children.
     */
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return false;
    }

    /**
     * Gets children.
     *
     * @param nodeTree the node tree
     * @return the array of children of this node.
     */
    @NotNull
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return EMPTY_ARRAY;
    }

    /**
     * Gets parent.
     *
     * @return the parent of this node.
     */
    @Nullable
    public ModelNode<?> getParent() {
        return parent;
    }

    /**
     * Sets parent.
     *
     * @param parent the parent.
     */
    protected void setParent(final ModelNode<?> parent) {
        this.parent = parent;
    }

    /**
     * Gets icon.
     *
     * @return the icon of this node.
     */
    @Nullable
    public Image getIcon() {
        return null;
    }

    /**
     * Fill the items actions for this node.
     *
     * @param nodeTree the node tree
     * @param items    the items
     */
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
    }

    /**
     * Remove the child from this node.
     *
     * @param child the child
     */
    public void remove(@NotNull final ModelNode<?> child) {
    }

    /**
     * Add the new child to this node.
     *
     * @param child the child
     */
    public void add(@NotNull final ModelNode<?> child) {
    }

    /**
     * Handle changing the name of this node.
     *
     * @param nodeTree the node tree
     * @param newName  the new name
     */
    public void changeName(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final String newName) {
    }

    /**
     * Can accept boolean.
     *
     * @param child the child
     * @return true of this node can accept the child.
     */
    public boolean canAccept(@NotNull final ModelNode<?> child) {
        return false;
    }

    /**
     * Can accept external boolean.
     *
     * @param dragboard the dragboard
     * @return true if this node can accept external resource.
     */
    public boolean canAcceptExternal(@NotNull final Dragboard dragboard) {
        return false;
    }

    /**
     * Accept external resources to this node.
     *
     * @param dragboard the dragboard
     * @param consumer  the consumer
     */
    public void acceptExternal(@NotNull final Dragboard dragboard, @NotNull final ChangeConsumer consumer) {
    }

    /**
     * Can move boolean.
     *
     * @return true if this node supports moving.
     */
    public boolean canMove() {
        return true;
    }

    /**
     * Can copy boolean.
     *
     * @return true if this node supports copying.
     */
    public boolean canCopy() {
        return false;
    }

    /**
     * Can edit name boolean.
     *
     * @return true if this node supports name editing.
     */
    public boolean canEditName() {
        return false;
    }

    /**
     * Can remove boolean.
     *
     * @return true if you can remove this node.
     */
    public boolean canRemove() {
        return true;
    }

    /**
     * Copy model node.
     *
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
        return getName();
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
