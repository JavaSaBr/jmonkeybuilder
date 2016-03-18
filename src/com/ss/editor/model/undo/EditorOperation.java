package com.ss.editor.model.undo;

/**
 * Интерфейс для реализации перации.
 *
 * @author Ronn
 */
public interface EditorOperation {

    /**
     * Метод для внесения изменений.
     */
    public default void redo(final UndoableEditor editor) {
    }

    /**
     * Метод для отката изменений.
     */
    public default void undo(final UndoableEditor editor) {
    }
}
