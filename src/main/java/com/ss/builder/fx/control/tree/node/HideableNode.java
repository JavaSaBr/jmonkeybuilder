package com.ss.builder.fx.control.tree.node;

import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.control.tree.NodeTree;

import org.jetbrains.annotations.NotNull;

/**
 * The interface-marker that an object can be hide.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public interface HideableNode<C extends ChangeConsumer> {

    /**
     * Is hided boolean.
     *
     * @return true if this object is hided.
     */
    boolean isHided();

    /**
     * Show the object.
     *
     * @param nodeTree the node tree
     */
    void show(@NotNull NodeTree<C> nodeTree);

    /**
     * Hide the object.
     *
     * @param nodeTree the node tree
     */
    void hide(@NotNull NodeTree<C> nodeTree);
}
