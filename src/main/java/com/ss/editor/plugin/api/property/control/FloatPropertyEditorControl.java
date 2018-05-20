package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.control.input.FloatTextField;
import org.jetbrains.annotations.NotNull;

/**
 * The control to edit float values.
 *
 * @author JavaSaBr
 */
public class FloatPropertyEditorControl extends TypedTextFieldPropertyEditorControl<Float, FloatTextField> {

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

    /**
     * Set min/max values.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(float min, float max) {
        if (Float.isNaN(min) || Float.isNaN(max)) return;
        getValueField().setMinMax(min, max);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        var value = getPropertyValue();
        getValueField().setValue(value == null ? 0 : value);
    }

    @Override
    @FxThread
    protected void changeImpl() {
        setPropertyValue(getValueField().getValue());
        super.changeImpl();
    }
}
