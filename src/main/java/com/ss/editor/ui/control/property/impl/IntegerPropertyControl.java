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
    @NotNull
    private final IntegerTextField valueField;

    public IntegerPropertyControl(
            @Nullable Integer propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.valueField = new IntegerTextField();
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

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(int min, int max) {
        valueField.setMinMax(min, max);
    }

    /**
     * Set the scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FxThread
    public void setScrollPower(int scrollPower) {
        valueField.setScrollPower(scrollPower);
    }

    /**
     * Get the scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    public int getScrollPower() {
        return (int) valueField.getScrollPower();
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
