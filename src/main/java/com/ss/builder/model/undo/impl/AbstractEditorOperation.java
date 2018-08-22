package com.ss.builder.model.undo.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.EditorOperation;
import com.ss.builder.model.undo.UndoableEditor;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ClassUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of the {@link EditorOperation} to support a generic type of an editor.
 *
 * @param <E> the change consumer's type.
 * @author JavaSabr
 */
public abstract class AbstractEditorOperation<E> implements EditorOperation {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorOperation.class);

    public AbstractEditorOperation() {
    }

    @Override
    @FxThread
    public void redo(@NotNull UndoableEditor editor) {
        redoImpl(ClassUtils.unsafeCast(editor));
    }

    /**
     * Execute changes.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void redoImpl(@NotNull E editor) {
        startInFx(editor);
        startRedoInFx(editor);

        ExecutorManager.getInstance()
                .addJmeTask(() -> {
                        startInJme(editor);
                        redoInJme(editor);
                        endInJme(editor);
                        ExecutorManager.getInstance()
                                .addFxTask(() -> {
                                        endRedoInFx(editor);
                                        endInFx(editor);
                                });
                });
    }

    /**
     * Start executing general changes in Fx thread.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void startInFx(@NotNull E editor) {

    }

    /**
     * Finish executing general changes in Fx thread.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void endInFx(@NotNull E editor) {

    }

    /**
     * Start executing general changes in jME thread.
     *
     * @param editor the editor.
     */
    @JmeThread
    protected void startInJme(@NotNull E editor) {

    }

    /**
     * Finish executing general changes in jME thread.
     *
     * @param editor the editor.
     */
    @JmeThread
    protected void endInJme(@NotNull E editor) {

    }

    /**
     * Start executing changes in Fx thread.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void startRedoInFx(@NotNull E editor) {

    }

    /**
     * Execute changes in jME thread.
     *
     * @param editor the editor.
     */
    @JmeThread
    protected void redoInJme(@NotNull E editor) {

    }

    /**
     * Finish executing changes in Fx thread.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void endRedoInFx(@NotNull E editor) {

    }


    @Override
    @FxThread
    public void undo(@NotNull UndoableEditor editor) {
        undoImpl(ClassUtils.unsafeCast(editor));
    }

    /**
     * Revert changes.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void undoImpl(@NotNull E editor) {
        startInFx(editor);
        startUndoInFx(editor);

        ExecutorManager.getInstance()
                .addJmeTask(() -> {
                        startInJme(editor);
                        undoInJme(editor);
                        endInJme(editor);
                        ExecutorManager.getInstance()
                                .addFxTask(() -> {
                                        endUndoInFx(editor);
                                        endInFx(editor);
                                });
                });
    }

    /**
     * Start reverting changes in Fx thread.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void startUndoInFx(@NotNull E editor) {

    }

    /**
     * Execute reverting in jME thread.
     *
     * @param editor the editor.
     */
    @JmeThread
    protected void undoInJme(@NotNull E editor) {

    }

    /**
     * Finish reverting changes in Fx thread.
     *
     * @param editor the editor.
     */
    @FxThread
    protected void endUndoInFx(@NotNull E editor) {

    }
}
