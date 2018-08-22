package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.util.ControlUtils;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractEditorOperation} to enable {@link com.jme3.scene.control.AbstractControl} in {@link Node}.
 *
 * @author JavaSaBr.
 */
public class EnableControlsOperation extends ChangeControlsOperation {

    public EnableControlsOperation(@NotNull Array<Control> controls) {
        super(controls);
    }

    @Override
    @JmeThread
    protected void redoChange(@NotNull Control control) {
        super.redoChange(control);
        ControlUtils.setEnabled(control, true);
    }

    @Override
    @JmeThread
    protected void undoChange(@NotNull Control control) {
        super.undoChange(control);
        ControlUtils.setEnabled(control, false);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getPropertyName() {
        return Messages.MODEL_PROPERTY_IS_ENABLED;
    }
}
