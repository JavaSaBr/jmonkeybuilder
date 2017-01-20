package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.layout.HBox;
import rlib.function.SixObjectConsumer;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit float values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFloatPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, Float> {

    /**
     * The filed with current value.
     */
    private FloatTextField valueField;

    public AbstractFloatPropertyControl(@Nullable final Float propertyValue, @NotNull final String propertyName,
                                        @NotNull final C changeConsumer,
                                        @NotNull final SixObjectConsumer<C, T, String, Float, Float, BiConsumer<T, Float>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new FloatTextField();
        valueField.setId(CSSIds.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
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
     * @return the scroll power.
     */
    @FXThread
    public float getScrollPower() {
        return valueField.getScrollPower();
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
