package com.ss.editor.ui.control.model.tree.node;

/**
 * The interface-marker that an object can be hide.
 *
 * @author JavaSaBr
 */
public interface Hideable {

    /**
     * @return true if this object is hided.
     */
    boolean isHided();

    /**
     * Show the object.
     */
    void show();

    /**
     * Hide the object.
     */
    void hide();
}
