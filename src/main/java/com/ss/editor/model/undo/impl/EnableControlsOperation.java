package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.util.ControlUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayCollectors;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractEditorOperation} to enable {@link com.jme3.scene.control.AbstractControl} in a {@link Node}.
 *
 * @author JavaSaBr.
 */
public class EnableControlsOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The controls to enable.
     */
    @NotNull
    private final Array<Control> controls;

    /**
     * The list of disabled controls.
     */
    @NotNull
    private final Array<Control> wasDisabled;

    public EnableControlsOperation(@NotNull final Array<Control> controls) {
        this.controls = controls;
        this.wasDisabled = controls.stream()
            .filter(control -> !ControlUtils.isEnabled(control))
            .collect(ArrayCollectors.toArray(Control.class));
    }

    @Override
    @JmeThread
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {

            for (final Control control : wasDisabled) {
                ControlUtils.setEnabled(control, true);
            }

            EXECUTOR_MANAGER.addFxTask(() -> {
                wasDisabled.forEach(editor, (control, consumer) ->
                        consumer.notifyFxChangeProperty(control, Messages.MODEL_PROPERTY_IS_ENABLED));
            });
        });
    }

    @Override
    @JmeThread
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {

            for (final Control control : wasDisabled) {
                ControlUtils.setEnabled(control, false);
            }

            wasDisabled.clear();

            EXECUTOR_MANAGER.addFxTask(() -> {
                wasDisabled.forEach(editor, (control, consumer) ->
                    consumer.notifyFxChangeProperty(control, Messages.MODEL_PROPERTY_IS_ENABLED));
            });
        });
    }
}
