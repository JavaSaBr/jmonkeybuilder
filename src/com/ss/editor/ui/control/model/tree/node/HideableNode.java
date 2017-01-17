package com.ss.editor.ui.control.model.tree.node;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;

import org.jetbrains.annotations.NotNull;

/**
 * The interface-marker that an object can be hide.
 *
 * @author JavaSaBr
 */
public interface HideableNode {

    /**
     * @return true if this object is hided.
     */
    boolean isHided();

    /**
     * Show the object.
     */
    void show(@NotNull ModelNodeTree nodeTree);

    /**
     * Hide the object.
     */
    void hide(@NotNull ModelNodeTree nodeTree);
}
