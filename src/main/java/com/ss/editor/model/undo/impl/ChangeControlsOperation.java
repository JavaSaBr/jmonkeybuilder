package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
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

    public ChangeControlsOperation(@NotNull Array<Control> controls) {
        this.controls = controls;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        controls.forEachR(this, ChangeControlsOperation::redoChange);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        controls.forEachR(this, ChangeControlsOperation::undoChange);
    }

    @Override
    @FxThread
    protected void endInFx(@NotNull ModelChangeConsumer editor) {
        super.endInFx(editor);
        controls.forEachRm(editor, getPropertyName(), ChangeConsumer::notifyFxChangeProperty);
    }

    /**
     * Apply new changes to the control.
     *
     * @param control the control.
     */
    @JmeThread
    protected void redoChange(@NotNull Control control) {
    }

    /**
     * Revert changes for the control.
     *
     * @param control the control.
     */
    @JmeThread
    protected void undoChange(@NotNull Control control) {
    }

    /**
     * Get the property name.
     *
     * @return the property name.
     */
    @FromAnyThread
    protected @NotNull String getPropertyName() {
        throw new UnsupportedOperationException();
    }
}
