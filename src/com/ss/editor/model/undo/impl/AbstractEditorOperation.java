package com.ss.editor.model.undo.impl;

import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.UndoableEditor;

/**
 * Базовая реализация операции с редактором.
 *
 * @author Ronn
 */
public abstract class AbstractEditorOperation<E> implements EditorOperation {

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();
    
    public AbstractEditorOperation() {
    }

    @Override
    public void redo(final UndoableEditor editor) {
        redoImpl((E) editor);
    }

    /**
     * Реализация внесения изменений.
     */
    protected abstract void redoImpl(E editor);

    @Override
    public void undo(final UndoableEditor editor) {
        undoImpl((E) editor);
    }

    /**
     * Реализация отката изменений.
     */
    protected abstract void undoImpl(final E editor);
}
