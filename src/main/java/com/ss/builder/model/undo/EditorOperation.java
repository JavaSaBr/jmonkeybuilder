package com.ss.builder.model.undo;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;

import org.jetbrains.annotations.NotNull;

/**
 * The interface for implementing an operation in an editor.
 *
 * @author JavaSabr
 */
public interface EditorOperation {

    /**
     * Redo this operation for the editor.
     *
     * @param editor the editor.
     */
    @FxThread
    default void redo(@NotNull UndoableEditor editor) {
    }

    /**
     * Undo this operation for the editor.
     *
     * @param editor the editor.
     */
    @FxThread
    default void undo(@NotNull UndoableEditor editor) {
    }
}
