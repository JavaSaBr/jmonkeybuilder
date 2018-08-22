package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.control.input.NumberTextField;
import org.jetbrains.annotations.NotNull;

/**
 * The property control to edit integer values.
 *
 * @author JavaSaBr
 */
public abstract class NumberPropertyEditorControl<T extends Number, F extends NumberTextField<T>> extends
        TypedTextFieldPropertyEditorControl<T, F> {

    public NumberPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    /**
     * Set min/max values.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(T min, T max) {
        valueField.setMinMax(min, max);
    }

    @Override
    @FxThread
    protected void changedImpl() {
        setPropertyValue(valueField.getValue());
        super.changedImpl();
    }
}
