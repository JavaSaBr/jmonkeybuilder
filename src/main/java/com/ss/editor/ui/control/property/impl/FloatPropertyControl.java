package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.NumberUtils.zeroIfNull;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.NumberUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The implementation of the {@link PropertyControl} to edit float values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class FloatPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, Float> {

    /**
     * The filed with current value.
     */
    @Nullable
    private FloatTextField valueField;

    public FloatPropertyControl(
            @Nullable Float propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {

        super(propertyValue, propertyName, changeConsumer);
    }

    public FloatPropertyControl(
            @Nullable Float propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, Float> changeHandler
    ) {

        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(getValueField(),
            widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        valueField = new FloatTextField();
        valueField.prefWidthProperty()
            .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
        valueField.focusedProperty()
            .addListener((observable, oldValue, newValue) -> applyOnLostFocus(newValue));

        FxControlUtils.onValueChange(valueField, this::updateValue);

        FXUtils.addClassTo(valueField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, container);
    }

    /**
     * Set the scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FxThread
    public void setScrollPower(float scrollPower) {
        getValueField().setScrollPower(scrollPower);
    }

    /**
     * Get the scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    public float getScrollPower() {
        return getValueField().getScrollPower();
    }

    /**
     * Set the value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(float min, float max) {
        getValueField().setMinMax(min, max);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Get the filed with current value.
     *
     * @return the filed with current value.
     */
    @FxThread
    private @NotNull FloatTextField getValueField() {
        return notNull(valueField);
    }

    @Override
    @FxThread
    protected void reload() {
        var valueField = getValueField();
        var caretPosition = valueField.getCaretPosition();
        valueField.setValue(zeroIfNull(getPropertyValue()));
        valueField.positionCaret(caretPosition);
    }

    @Override
    @FxThread
    public boolean isDirty() {
        return !Objects.equals(getValueField().getValue(), getPropertyValue());
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue() {
        if (!isIgnoreListener()) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();

        var currentValue = getValueField().getValue();
        var storedValue = getPropertyValue();

        changed(currentValue, storedValue);
    }
}
