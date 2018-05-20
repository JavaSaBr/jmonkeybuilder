package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.ifNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.control.input.IntegerTextField;
import org.jetbrains.annotations.NotNull;

/**
 * The property control to edit integer values.
 *
 * @author JavaSaBr
 */
public class IntegerPropertyEditorControl extends TypedTextFieldPropertyEditorControl<Integer, IntegerTextField> {

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

    /**
     * Set min/max values.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(float min, float max) {
        if (Float.isNaN(min) || Float.isNaN(max)) return;
        getValueField().setMinMax((int) min, (int) max);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        getValueField().setValue(ifNull(getPropertyValue(), 0));
    }

    @Override
    @FxThread
    protected void changeImpl() {
        setPropertyValue(getValueField().getValue());
        super.changeImpl();
    }
}
