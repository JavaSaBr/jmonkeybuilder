package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.ExtMath;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.control.input.FloatTextField;
import org.jetbrains.annotations.NotNull;

/**
 * The control to edit float values.
 *
 * @author JavaSaBr
 */
public class FloatPropertyEditorControl extends NumberPropertyEditorControl<Float, FloatTextField> {

    public FloatPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected @NotNull FloatTextField createField() {
        return new FloatTextField();
    }

    @Override
    @FxThread
    protected void reloadImpl() {
        valueField.setValue(ExtMath.zeroIfNull(getPropertyValue()));
        super.reloadImpl();
    }
}
