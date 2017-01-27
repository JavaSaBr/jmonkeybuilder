package com.ss.editor.ui.control.tree.node;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTree;

import org.jetbrains.annotations.NotNull;

/**
 * The interface-marker that an object can be hide.
 *
 * @author JavaSaBr
 */
public interface HideableNode<C extends ChangeConsumer> {

    /**
     * @return true if this object is hided.
     */
    boolean isHided();

    /**
     * Show the object.
     */
    void show(@NotNull AbstractNodeTree<C> nodeTree);

    /**
     * Hide the object.
     */
    void hide(@NotNull AbstractNodeTree<C> nodeTree);
}
