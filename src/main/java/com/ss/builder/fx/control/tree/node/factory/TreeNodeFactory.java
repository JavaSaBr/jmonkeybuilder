package com.ss.builder.fx.control.tree.node.factory;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The factory to create the {@link TreeNode} of an element.
 *
 * @author JavaSabr
 */
public interface TreeNodeFactory extends Comparable<TreeNodeFactory> {

    /**
     * Create a tree node for an element.
     *
     * @param <T>      the type of an element.
     * @param <V>      the type of a tree node.
     * @param element  the element.
     * @param objectId the object id.
     * @return the tree node.
     */
    @FxThread
    <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId);

    /**
     * Gets an order of this factory.
     *
     * @return the order.
     */
    @FxThread
    default int getPriority() {
        return 0;
    }

    @Override
    default int compareTo(@NotNull final TreeNodeFactory another) {
        return another.getPriority() - getPriority();
    }
}
