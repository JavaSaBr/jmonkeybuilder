package com.ss.editor.model.undo;

import org.jetbrains.annotations.NotNull;

/**
 * The interface for implementing an operation in an editor.
 *
 * @author JavaSabr
 */
public interface EditorOperation {

    /**
     * Redo this operation for the editor.
     */
    default void redo(@NotNull final UndoableEditor editor) {
    }

    /**
     * Undo this operation for the editor.
     */
    default void undo(@NotNull final UndoableEditor editor) {
    }
}
