package com.ss.editor.ui.control.model.property;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.HBox;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for editing float values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFloatModelPropertyControl<T> extends ModelPropertyControl<T, Float> {

    /**
     * The filed with current value.
     */
    private FloatTextField valueField;

    public AbstractFloatModelPropertyControl(final Float element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new FloatTextField();
        valueField.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        valueField.addChangeListener((observable, oldValue, newValue) -> updateValue());
        valueField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addToPane(valueField, container);
    }

    /**
     * @param scrollPower the scroll power.
     */
    @FXThread
    public void setScrollPower(final float scrollPower) {
        valueField.setScrollPower(scrollPower);
    }

    /**
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FXThread
    public void setMinMax(final float min, final float max) {
        valueField.setMinMax(min, max);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the filed with current value.
     */
    private FloatTextField getValueField() {
        return valueField;
    }

    @Override
    protected void reload() {
        final Float value = getPropertyValue();
        final FloatTextField valueField = getValueField();
        final int caretPosition = valueField.getCaretPosition();
        valueField.setValue(value == null ? 0F : value);
        valueField.positionCaret(caretPosition);
    }

    /**
     * Update the value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;

        final FloatTextField valueField = getValueField();
        final float value = valueField.getValue();

        final Float oldValue = getPropertyValue();
        changed(value, oldValue);
    }
}
