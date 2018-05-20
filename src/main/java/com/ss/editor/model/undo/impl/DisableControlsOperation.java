package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.util.ControlUtils;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractEditorOperation} to disable {@link com.jme3.scene.control.AbstractControl} in {@link Node}.
 *
 * @author JavaSaBr.
 */
public class DisableControlsOperation extends ChangeControlsOperation {

    public DisableControlsOperation(@NotNull final Array<Control> controls) {
        super(controls);
    }

    @Override
    protected void redoChange(final @NotNull Control control) {
        super.redoChange(control);
        ControlUtils.setEnabled(control, false);
    }

    @Override
    protected void undoChange(final @NotNull Control control) {
        super.undoChange(control);
        ControlUtils.setEnabled(control, true);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return Messages.MODEL_PROPERTY_IS_ENABLED;
    }
}
