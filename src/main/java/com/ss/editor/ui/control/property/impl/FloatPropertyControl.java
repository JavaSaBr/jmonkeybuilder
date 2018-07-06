package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ExtMath.zeroIfNull;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.control.input.FloatTextField;
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
    @NotNull
    private final FloatTextField valueField;

    public FloatPropertyControl(
            @Nullable Float propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.valueField = new FloatTextField();
    }

    public FloatPropertyControl(
            @Nullable Float propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, Float> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        this.valueField = new FloatTextField();
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(valueField,
                widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onValueChange(valueField, this::updateValue);
        FxControlUtils.onFocusChange(valueField, this::applyOnLostFocus);

        FxUtils.addClass(valueField, CssClasses.PROPERTY_CONTROL_COMBO_BOX);
        FxUtils.addChild(container, valueField);
    }

    /**
     * Set the scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FxThread
    public void setScrollPower(float scrollPower) {
        valueField.setScrollPower(scrollPower);
    }

    /**
     * Get the scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    public float getScrollPower() {
        return valueField.getScrollPower();
    }

    /**
     * Set the value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(float min, float max) {
        valueField.setMinMax(min, max);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        var caretPosition = valueField.getCaretPosition();
        valueField.setValue(zeroIfNull(getPropertyValue()));
        valueField.positionCaret(caretPosition);

        super.reloadImpl();
    }

    @Override
    @FxThread
    public boolean isDirty() {
        return !Objects.equals(valueField.getValue(), getPropertyValue());
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
        changed(valueField.getValue(), getPropertyValue());
    }
}
