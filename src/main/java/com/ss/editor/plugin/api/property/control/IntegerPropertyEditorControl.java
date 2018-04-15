package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.control.input.IntegerTextField;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.VarTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit integer values.
 *
 * @author JavaSaBr
 */
public class IntegerPropertyEditorControl extends PropertyEditorControl<Integer> {

    /**
     * The value field.
     */
    @Nullable
    private IntegerTextField valueField;

    protected IntegerPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                           @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        valueField = new IntegerTextField();
        valueField.addChangeListener((observable, oldValue, newValue) -> change());
        valueField.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FXUtils.addClassTo(valueField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, this);
    }

    /**
     * Set min/max values.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(final float min, final float max) {
        if (Float.isNaN(min) || Float.isNaN(max)) return;
        getValueField().setMinMax((int) min, (int) max);
    }

    /**
     * @return the value field.
     */
    @FxThread
    private @NotNull IntegerTextField getValueField() {
        return notNull(valueField);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        final Integer value = getPropertyValue();
        getValueField().setValue(value == null ? 0 : value);
    }

    @Override
    @FxThread
    protected void changeImpl() {
        setPropertyValue(getValueField().getValue());
        super.changeImpl();
    }
}
