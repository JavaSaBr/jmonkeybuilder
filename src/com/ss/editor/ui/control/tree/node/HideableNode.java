package com.ss.editor.ui.control.tree.node;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTree;

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
    void show(@NotNull AbstractNodeTree<C> nodeTree);

    /**
     * Hide the object.
     *
     * @param nodeTree the node tree
     */
    void hide(@NotNull AbstractNodeTree<C> nodeTree);
}
