package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractEditorOperation} to chane {@link com.jme3.scene.control.AbstractControl} in {@link Node}.
 *
 * @author JavaSaBr.
 */
public class ChangeControlsOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The controls to change.
     */
    @NotNull
    private final Array<Control> controls;

    public ChangeControlsOperation(@NotNull final Array<Control> controls) {
        this.controls = controls;
    }

    @Override
    @JmeThread
    protected void redoInFx(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {

            for (final Control control : controls) {
                redoChange(control);
            }

            EXECUTOR_MANAGER.addFxTask(() -> {
                controls.forEach(editor, (control, consumer) ->
                        consumer.notifyFxChangeProperty(control, getPropertyName()));
            });
        });
    }

    /**
     * Apply new changes to the control.
     *
     * @param control the control.
     */
    @JmeThread
    protected void redoChange(@NotNull final Control control) {
    }

    @Override
    @JmeThread
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {

            for (final Control control : controls) {
                undoChange(control);
            }

            EXECUTOR_MANAGER.addFxTask(() -> {
                controls.forEach(editor, (control, consumer) ->
                    consumer.notifyFxChangeProperty(control, getPropertyName()));
            });
        });
    }

    /**
     * Revert changes for the control.
     *
     * @param control the control.
     */
    @JmeThread
    protected void undoChange(@NotNull final Control control) {
    }

    /**
     * Get the property name.
     *
     * @return the property name.
     */
    @FxThread
    protected @NotNull String getPropertyName() {
        throw new UnsupportedOperationException();
    }
}
