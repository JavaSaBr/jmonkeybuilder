package com.ss.builder.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.util.ControlUtils;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractEditorOperation} to disable {@link com.jme3.scene.control.AbstractControl} in {@link Node}.
 *
 * @author JavaSaBr.
 */
public class DisableControlsOperation extends ChangeControlsOperation {

    public DisableControlsOperation(@NotNull Array<Control> controls) {
        super(controls);
    }

    @Override
    @JmeThread
    protected void redoChange(@NotNull Control control) {
        super.redoChange(control);
        ControlUtils.setEnabled(control, false);
    }

    @Override
    @JmeThread
    protected void undoChange(@NotNull Control control) {
        super.undoChange(control);
        ControlUtils.setEnabled(control, true);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getPropertyName() {
        return Messages.MODEL_PROPERTY_IS_ENABLED;
    }
}
