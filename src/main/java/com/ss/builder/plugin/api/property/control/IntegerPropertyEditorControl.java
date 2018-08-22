package com.ss.builder.plugin.api.property.control;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.ExtMath;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.control.input.IntegerTextField;
import org.jetbrains.annotations.NotNull;

/**
 * The property control to edit integer values.
 *
 * @author JavaSaBr
 */
public class IntegerPropertyEditorControl extends NumberPropertyEditorControl<Integer, IntegerTextField> {

    public IntegerPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected @NotNull IntegerTextField createField() {
        return new IntegerTextField();
    }

    @Override
    @FxThread
    protected void reloadImpl() {
        valueField.setValue(ExtMath.zeroIfNull(getPropertyValue()));
        super.reloadImpl();
    }
}
