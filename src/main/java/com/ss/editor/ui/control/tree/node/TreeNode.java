package com.ss.editor.ui.control.tree.node;

import static com.ss.editor.ui.control.tree.NodeTreeCell.DATA_FORMAT;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.UObject;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.CopyNodeAction;
import com.ss.editor.ui.control.model.tree.action.PasteNodeAction;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.Dragboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The base implementation of a tree node.
 *
 * @param <T> the type of presented element
 * @author JavaSaBr
 */
public abstract class TreeNode<T> implements UObject {

    /**
     * The constant EMPTY_ARRAY.
     */
    @NotNull
    protected static final Array<TreeNode<?>> EMPTY_ARRAY = ArrayFactory.newArray(TreeNode.class);

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final JmeApplication JME_APPLICATION = JmeApplication.getInstance();

    /**
     * The constant FACTORY_REGISTRY.
     */
    @NotNull
    protected static final TreeNodeFactoryRegistry FACTORY_REGISTRY = TreeNodeFactoryRegistry.getInstance();

    /**
     * The uniq id of this node.
     */
    private final long objectId;

    /**
     * The element of the {@link com.jme3.scene.Spatial}.
     */
    @NotNull
    private final T element;

    /**
     * The parent.
     */
    @Nullable
    private TreeNode<?> parent;

    /**
     * Instantiates a new Model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public TreeNode(@NotNull final T element, final long objectId) {
        this.element = element;
        this.objectId = objectId;
    }

    /**
     * Gets element.
     *
     * @return the element of the {@link com.jme3.scene.Spatial}.
     */
    @FromAnyThread
    public @NotNull T getElement() {
        return element;
    }

    /**
     * Gets name.
     *
     * @return the name of this node.
     */
    @FromAnyThread
    public @NotNull String getName() {
        return "unknown name";
    }

    /**
     * Sets name.
     *
     * @param name the name.
     */
    @FXThread
    public void setName(@NotNull final String name) {
    }

    /**
     * Is need to save name.
     *
     * @return true if need to save name.
     */
    @FromAnyThread
    public boolean isNeedToSaveName() {
        return false;
    }

    /**
     * Has children boolean.
     *
     * @param nodeTree the node tree
     * @return true of this node has any children.
     */
    @FXThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return false;
    }

    /**
     * Gets children.
     *
     * @param nodeTree the node tree
     * @return the array of children of this node.
     */
    @FXThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {
        return EMPTY_ARRAY;
    }

    /**
     * Gets parent.
     *
     * @return the parent of this node.
     */
    @FXThread
    public @Nullable TreeNode<?> getParent() {
        return parent;
    }

    /**
     * Sets parent.
     *
     * @param parent the parent.
     */
    @FXThread
    protected void setParent(@Nullable final TreeNode<?> parent) {
        this.parent = parent;
    }

    /**
     * Gets icon.
     *
     * @return the icon of this node.
     */
    @FXThread
    public @Nullable Image getIcon() {
        return null;
    }

    /**
     * Fill the items actions for this node.
     *
     * @param nodeTree the node tree
     * @param items    the items
     */
    @FXThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        if (canEditName()) items.add(new RenameNodeAction(nodeTree, this));
        if (canCopy()) items.add(new CopyNodeAction(nodeTree, this));

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final Object content = clipboard.getContent(DATA_FORMAT);
        if (!(content instanceof Long)) {
            return;
        }

        final Long objectId = (Long) content;
        final TreeItem<?> treeItem = UIUtils.findItem(nodeTree.getTreeView(), objectId);
        final TreeNode<?> treeNode = treeItem == null ? null : (TreeNode<?>) treeItem.getValue();

        if (treeNode != null && canAccept(treeNode, true)) {
            items.add(new PasteNodeAction(nodeTree, this, treeNode));
        }
    }

    /**
     * Remove the child from this node.
     *
     * @param child the child
     */
    @FXThread
    public void remove(@NotNull final TreeNode<?> child) {
    }

    /**
     * Add the new child to this node.
     *
     * @param child the child
     */
    @FXThread
    public void add(@NotNull final TreeNode<?> child) {
    }

    /**
     * Handle changing the name of this node.
     *
     * @param nodeTree the node tree
     * @param newName  the new name
     */
    @FXThread
    public void changeName(@NotNull final NodeTree<?> nodeTree, @NotNull final String newName) {
    }

    /**
     * Check of possibility to accept the tree node as a new child.
     *
     * @param treeNode the node.
     * @param isCopy   true if need to copy the node.
     * @return true if this node can be accept.
     */
    @FXThread
    public boolean canAccept(@NotNull final TreeNode<?> treeNode, final boolean isCopy) {
        return false;
    }

    /**
     * Accept the object to this node.
     *
     * @param changeConsumer the change consumer.
     * @param object         the object.
     * @param isCopy         true if need to copy the object.
     */
    @FXThread
    public void accept(@NotNull final ChangeConsumer changeConsumer, @NotNull final Object object,
                       final boolean isCopy) {
    }

    /**
     * Can accept external boolean.
     *
     * @param dragboard the dragboard
     * @return true if this node can accept external resource.
     */
    @FXThread
    public boolean canAcceptExternal(@NotNull final Dragboard dragboard) {
        return false;
    }

    /**
     * Accept external resources to this node.
     *
     * @param dragboard the dragboard
     * @param consumer  the consumer
     */
    @FXThread
    public void acceptExternal(@NotNull final Dragboard dragboard, @NotNull final ChangeConsumer consumer) {
    }

    /**
     * Can move boolean.
     *
     * @return true if this node supports moving.
     */
    @FXThread
    public boolean canMove() {
        return false;
    }

    /**
     * Can copy boolean.
     *
     * @return true if this node supports copying.
     */
    @FXThread
    public boolean canCopy() {
        return false;
    }

    /**
     * Can edit name boolean.
     *
     * @return true if this node supports name editing.
     */
    @FXThread
    public boolean canEditName() {
        return false;
    }

    /**
     * Can remove boolean.
     *
     * @return true if you can remove this node.
     */
    @FXThread
    public boolean canRemove() {
        return true;
    }

    /**
     * Copy model node.
     *
     * @return the new copy of this node.
     */
    @FXThread
    public @NotNull TreeNode<?> copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    @FromAnyThread
    public long getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(@Nullable final Object other) {

        if (this == other) return true;
        if (other == null) return false;

        if (other instanceof TreeNode) {
            TreeNode<?> treeNode = (TreeNode<?>) other;
            return element.equals(treeNode.element);
        }

        return Objects.equals(element, other);
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

    /**
     * Notify about that a model node was added to children of this node.
     *
     * @param treeNode the model node.
     */
    @FXThread
    public void notifyChildAdded(@NotNull final TreeNode<?> treeNode) {
    }

    /**
     * Notify about that a model node was removed from children of this node.
     *
     * @param treeNode the model node.
     */
    @FXThread
    public void notifyChildRemoved(@NotNull final TreeNode<?> treeNode) {
        treeNode.setParent(null);
    }

    /**
     * Notify about that a model node will add to children of this node.
     *
     * @param treeNode the model node.
     */
    @FXThread
    public void notifyChildPreAdd(@NotNull final TreeNode<?> treeNode) {
        treeNode.setParent(this);
    }

    /**
     * Notify about that a model node will remove from children of this node.
     *
     * @param treeNode the model node.
     */
    @FXThread
    public void notifyChildPreRemove(@NotNull final TreeNode<?> treeNode) {
    }
}
