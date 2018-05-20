package com.ss.editor.model.undo.impl;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of the {@link EditorOperation} to support a generic type of an editor.
 *
 * @param <E> the change consumer's type.
 * @author JavaSabr
 */
public abstract class AbstractEditorOperation<E> implements EditorOperation {

    /**
     * The logger.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(EditorOperation.class);

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    public AbstractEditorOperation() {
    }

    @Override
    @FxThread
    public void redo(@NotNull final UndoableEditor editor) {
        redoImpl(unsafeCast(editor));
    }

    /**
     * Execute changes.
     *
     * @param editor the editor.
     */
    @FxThread
    protected abstract void redoImpl(@NotNull E editor);

    @Override
    @FxThread
    public void undo(@NotNull final UndoableEditor editor) {
        undoImpl(unsafeCast(editor));
    }

    /**
     * Revert changes.
     *
     * @param editor the editor.
     */
    @FxThread
    protected abstract void undoImpl(@NotNull final E editor);
}
