package com.ss.builder.ui.control.tree.node;

import static com.ss.editor.ui.control.tree.NodeTreeCell.DATA_FORMAT;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.UObject;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.ui.control.tree.action.AbstractNodeAction;
import com.ss.builder.ui.control.tree.action.impl.CopyNodeAction;
import com.ss.builder.ui.control.tree.action.impl.PasteNodeAction;
import com.ss.builder.ui.control.tree.action.impl.RenameNodeAction;
import com.ss.builder.ui.control.tree.node.factory.TreeNodeFactoryRegistry;
import com.ss.builder.ui.util.UiUtils;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.UObject;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.action.impl.CopyNodeAction;
import com.ss.editor.ui.control.tree.action.impl.PasteNodeAction;
import com.ss.editor.ui.control.tree.action.impl.RenameNodeAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactoryRegistry;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.Dragboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

/**
 * The base implementation of a tree node.
 *
 * @param <T> the type of presented element
 * @author JavaSaBr
 */
public abstract class TreeNode<T> implements UObject {

    protected static final TreeNodeFactoryRegistry FACTORY_REGISTRY = TreeNodeFactoryRegistry.getInstance();

    /**
     * The action's comparator.
     */
    protected static final Comparator<MenuItem> ACTION_COMPARATOR = (first, second) -> {
        if (first instanceof Menu) {
            return -1;
        } else if (second instanceof Menu) {
            return 1;
        } else if (first instanceof AbstractNodeAction) {
            return ((AbstractNodeAction) first).compareTo(second);
        } else {
            return 0;
        }
    };

    /**
     * The uniq id of this node.
     */
    private final long objectId;

    /**
     * The wrapped element.
     */
    @NotNull
    private final T element;

    /**
     * The parent node.
     */
    @Nullable
    private TreeNode<?> parent;

    public TreeNode(@NotNull T element, long objectId) {
        this.element = element;
        this.objectId = objectId;
    }

    /**
     * Get the wrapped element.
     *
     * @return the wrapped element.
     */
    @FromAnyThread
    public @NotNull T getElement() {
        return element;
    }

    /**
     * Get the name of this node.
     *
     * @return the name of this node.
     */
    @FromAnyThread
    public @NotNull String getName() {
        return "unknown name";
    }

    /**
     * Set the name of this node.
     *
     * @param name the name of this node.
     */
    @FxThread
    public void setName(@NotNull String name) {
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
    @FxThread
    public boolean hasChildren(@NotNull NodeTree<?> nodeTree) {
        return false;
    }

    /**
     * Get the  children.
     *
     * @param nodeTree the node tree
     * @return the array of children of this node.
     */
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull NodeTree<?> nodeTree) {
        return Array.empty();
    }

    /**
     * Get the parent.
     *
     * @return the parent of this node or null.
     */
    @FxThread
    public @Nullable TreeNode<?> getParent() {
        return parent;
    }

    /**
     * Set the parent node.
     *
     * @param parent the parent node.
     */
    @FxThread
    protected void setParent(@Nullable TreeNode<?> parent) {
        this.parent = parent;
    }

    /**
     * Get the icon of this node.
     *
     * @return the icon of this node or null.
     */
    @FxThread
    public @Nullable Image getIcon() {
        return null;
    }

    /**
     * Fill the items actions for this node.
     *
     * @param nodeTree the node tree
     * @param items    the items
     */
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {

        if (canEditName()) {
            items.add(new RenameNodeAction(nodeTree, this));
        }

        if (canCopy()) {
            items.add(new CopyNodeAction(nodeTree, this));
        }

        var clipboard = Clipboard.getSystemClipboard();
        // added good method to fx lib
        var content = clipboard.getContent(DATA_FORMAT);
        if (!(content instanceof Long)) {
            return;
        }

        var objectId = (Long) content;
        var treeItem = UiUtils.findItem(nodeTree.getTreeView(), objectId);
        var treeNode = treeItem == null ? null : treeItem.getValue();

        if (treeNode != null && canAccept(treeNode, true)) {
            items.add(new PasteNodeAction(nodeTree, this, treeNode));
        }
    }

    /**
     * Handle the result context menu.
     *
     * @param nodeTree the node tree.
     * @param items    the result items.
     */
    @FxThread
    public void handleResultContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {
        items.sort(ACTION_COMPARATOR);
    }

    /**
     * Remove the child from this node.
     *
     * @param child the child
     */
    @FxThread
    public void remove(@NotNull TreeNode<?> child) {
    }

    /**
     * Add the new child to this node.
     *
     * @param child the child
     */
    @FxThread
    public void add(@NotNull TreeNode<?> child) {
    }

    /**
     * Handle changing the name of this node.
     *
     * @param nodeTree the node tree
     * @param newName  the new name
     */
    @FxThread
    public void changeName(@NotNull NodeTree<?> nodeTree, @NotNull String newName) {
    }

    /**
     * Check of possibility to accept the tree node as a new child.
     *
     * @param treeNode the node.
     * @param isCopy   true if need to copy the node.
     * @return true if this node can be accept.
     */
    @FxThread
    public boolean canAccept(@NotNull TreeNode<?> treeNode, boolean isCopy) {
        return false;
    }

    /**
     * Accept the object to this node.
     *
     * @param changeConsumer the change consumer.
     * @param object         the object.
     * @param isCopy         true if need to copy the object.
     */
    @FxThread
    public void accept(@NotNull ChangeConsumer changeConsumer, @NotNull Object object, boolean isCopy) {
    }

    /**
     * Can accept external boolean.
     *
     * @param dragboard the dragboard
     * @return true if this node can accept external resource.
     */
    @FxThread
    public boolean canAcceptExternal(@NotNull Dragboard dragboard) {
        return false;
    }

    /**
     * Accept external resources to this node.
     *
     * @param dragboard the dragboard
     * @param consumer  the consumer
     */
    @FxThread
    public void acceptExternal(@NotNull Dragboard dragboard, @NotNull ChangeConsumer consumer) {
    }

    /**
     * Can move boolean.
     *
     * @return true if this node supports moving.
     */
    @FxThread
    public boolean canMove() {
        return false;
    }

    /**
     * Can copy boolean.
     *
     * @return true if this node supports copying.
     */
    @FxThread
    public boolean canCopy() {
        return false;
    }

    /**
     * Can edit name boolean.
     *
     * @return true if this node supports name editing.
     */
    @FxThread
    public boolean canEditName() {
        return false;
    }

    /**
     * Can remove boolean.
     *
     * @return true if you can remove this node.
     */
    @FxThread
    public boolean canRemove() {
        return true;
    }

    /**
     * Copy model node.
     *
     * @return the new copy of this node.
     */
    @FxThread
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
    public boolean equals(@Nullable Object other) {

        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        }

        if (other instanceof TreeNode) {
            var treeNode = (TreeNode<?>) other;
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
    @FxThread
    public void notifyChildAdded(@NotNull TreeNode<?> treeNode) {
    }

    /**
     * Notify about that a model node was removed from children of this node.
     *
     * @param treeNode the model node.
     */
    @FxThread
    public void notifyChildRemoved(@NotNull TreeNode<?> treeNode) {
        treeNode.setParent(null);
    }

    /**
     * Notify about that a model node will add to children of this node.
     *
     * @param treeNode the model node.
     */
    @FxThread
    public void notifyChildPreAdd(@NotNull TreeNode<?> treeNode) {
        treeNode.setParent(this);
    }

    /**
     * Notify about that a model node will remove from children of this node.
     *
     * @param treeNode the model node.
     */
    @FxThread
    public void notifyChildPreRemove(@NotNull TreeNode<?> treeNode) {
    }
}
