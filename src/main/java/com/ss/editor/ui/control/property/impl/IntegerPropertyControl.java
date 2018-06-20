package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ExtMath.zeroIfNull;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.control.input.IntegerTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The implementation of the {@link PropertyControl} to edit integer values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class IntegerPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, Integer> {

    /**
     * The filed with current value.
     */
    @Nullable
    private IntegerTextField valueField;

    public IntegerPropertyControl(
            @Nullable Integer propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
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

        valueField = new IntegerTextField();
        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onValueChange(valueField, this::updateValue);
        FxControlUtils.onFocusChange(valueField, this::applyOnLostFocus);

        FxUtils.addClass(valueField,
                CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        FxUtils.addChild(container, valueField);
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
    private @NotNull IntegerTextField getValueField() {
        return notNull(valueField);
    }

    /**
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(int min, int max) {
        getValueField().setMinMax(min, max);
    }

    /**
     * Set the scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FxThread
    public void setScrollPower(int scrollPower) {
        getValueField().setScrollPower(scrollPower);
    }

    /**
     * Get the scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    public int getScrollPower() {
        return (int) getValueField().getScrollPower();
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
        changed(getValueField().getValue(), getPropertyValue());
    }
}
