package com.ss.builder.model.undo;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;

/**
 * The interface to implement an undoable editor.
 *
 * @author JavaSaBr
 */
public interface UndoableEditor {

    /**
     * Increment changes count.
     */
    @FxThread
    void incrementChange();

    /**
     * Decrement changes count.
     */
    @FxThread
    void decrementChange();

    /**
     * Redo the last operation.
     */
    @FromAnyThread
    void redo();

    /**
     * Undo the last operation.
     */
    @FromAnyThread
    void undo();
}
