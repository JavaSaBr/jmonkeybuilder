package com.ss.editor.model.undo;

import com.ss.editor.annotation.FxThread;

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
    default void redo(@NotNull final UndoableEditor editor) {
    }

    /**
     * Undo this operation for the editor.
     *
     * @param editor the editor.
     */
    @FxThread
    default void undo(@NotNull final UndoableEditor editor) {
    }
}
