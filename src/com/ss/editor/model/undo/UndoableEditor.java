package com.ss.editor.model.undo;

/**
 * The interface to implement an undoable editor.
 *
 * @author JavaSaBr
 */
public interface UndoableEditor {

    /**
     * Increment changes count.
     */
    void incrementChange();

    /**
     * Decrement changes count.
     */
    void decrementChange();
}
