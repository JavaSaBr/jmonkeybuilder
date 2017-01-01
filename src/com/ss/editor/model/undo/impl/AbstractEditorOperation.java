package com.ss.editor.model.undo.impl;

import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.UndoableEditor;

import org.jetbrains.annotations.NotNull;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * The base implementation of the {@link EditorOperation} for supporting the generic type of an editor.
 *
 * @author JavaSabr
 */
public abstract class AbstractEditorOperation<E> implements EditorOperation {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorOperation.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    public AbstractEditorOperation() {
    }

    @Override
    public void redo(@NotNull final UndoableEditor editor) {
        redoImpl(unsafeCast(editor));
    }

    /**
     * Execute changes.
     *
     * @param editor the editor.
     */
    protected abstract void redoImpl(@NotNull E editor);

    @Override
    public void undo(@NotNull final UndoableEditor editor) {
        undoImpl(unsafeCast(editor));
    }

    /**
     * Revert changes.
     *
     * @param editor the editor.
     */
    protected abstract void undoImpl(@NotNull final E editor);
}
