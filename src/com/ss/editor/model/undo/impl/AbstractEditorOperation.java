package com.ss.editor.model.undo.impl;

import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.UndoableEditor;

import org.jetbrains.annotations.NotNull;

import static rlib.util.ClassUtils.unsafeCast;

/**
 * The base implementation of the {@link EditorOperation} for supporting the generic type of an
 * editor.
 *
 * @author JavaSabr
 */
public abstract class AbstractEditorOperation<E> implements EditorOperation {

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    public AbstractEditorOperation() {
    }

    @Override
    public void redo(@NotNull final UndoableEditor editor) {
        redoImpl(unsafeCast(editor));
    }

    /**
     * Реализация внесения изменений.
     */
    protected abstract void redoImpl(@NotNull E editor);

    @Override
    public void undo(@NotNull final UndoableEditor editor) {
        undoImpl(unsafeCast(editor));
    }

    /**
     * Реализация отката изменений.
     */
    protected abstract void undoImpl(@NotNull final E editor);
}
