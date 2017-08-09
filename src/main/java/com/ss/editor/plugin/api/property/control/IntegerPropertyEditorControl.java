package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
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
    protected void createComponents() {
        super.createComponents();

        valueField = new IntegerTextField();
        valueField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        valueField.addChangeListener((observable, oldValue, newValue) -> change());
        valueField.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FXUtils.addClassTo(valueField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, this);
    }

    /**
     * Set min/max values.
     *
     * @param min the min value.
     * @param max the max value.
     */
    public void setMinMax(final float min, final float max) {
        if (Float.isNaN(min) || Float.isNaN(max)) return;
        getValueField().setMinMax((int) min, (int) max);
    }

    @NotNull
    private IntegerTextField getValueField() {
        return notNull(valueField);
    }

    @Override
    protected void reload() {
        super.reload();
        final Integer value = getPropertyValue();
        getValueField().setValue(value == null ? 0 : value);
    }

    @Override
    protected void change() {
        setPropertyValue(getValueField().getValue());
        super.change();
    }
}
