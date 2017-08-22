package com.ss.editor.model.undo.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.Editor;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.UndoableEditor;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;

/**
 * The base implementation of the {@link EditorOperation} to support a generic type of an editor.
 *
 * @param <E> the type parameter
 * @author JavaSabr
 */
public abstract class AbstractEditorOperation<E> implements EditorOperation {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(EditorOperation.class);

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The editor.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Instantiates a new Abstract editor operation.
     */
    public AbstractEditorOperation() {
    }

    @Override
    @FXThread
    public void redo(@NotNull final UndoableEditor editor) {
        redoImpl(unsafeCast(editor));
    }

    /**
     * Execute changes.
     *
     * @param editor the editor.
     */
    @FXThread
    protected abstract void redoImpl(@NotNull E editor);

    @Override
    @FXThread
    public void undo(@NotNull final UndoableEditor editor) {
        undoImpl(unsafeCast(editor));
    }

    /**
     * Revert changes.
     *
     * @param editor the editor.
     */
    @FXThread
    protected abstract void undoImpl(@NotNull final E editor);
}
