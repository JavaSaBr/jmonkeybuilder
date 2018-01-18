package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit integer values.
 *
 * @param <C> the type of {@link ChangeConsumer}
 * @param <T> the type of edited object
 * @author JavaSaBr
 */
public class IntegerPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, Integer> {

    /**
     * The filed with current value.
     */
    @Nullable
    private IntegerTextField valueField;

    public IntegerPropertyControl(@Nullable final Integer propertyValue, @NotNull final String propertyName,
                                  @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(final double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        final IntegerTextField valueField = getValueField();
        valueField.prefWidthProperty().unbind();
        valueField.prefWidthProperty().bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new IntegerTextField();
        valueField.addChangeListener((observable, oldValue, newValue) -> updateValue());
        valueField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, container);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
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
    public void setMinMax(final int min, final int max) {
        getValueField().setMinMax(min, max);
    }

    /**
     * Sets scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FxThread
    public void setScrollPower(final int scrollPower) {
        getValueField().setScrollPower(scrollPower);
    }

    /**
     * Gets scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    public int getScrollPower() {
        return getValueField().getScrollPower();
    }

    @Override
    @FxThread
    protected void reload() {
        final Integer element = getPropertyValue();
        final IntegerTextField valueField = getValueField();
        final int caretPosition = valueField.getCaretPosition();
        valueField.setText(String.valueOf(element));
        valueField.positionCaret(caretPosition);
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue() {
        if (isIgnoreListener()) return;

        final IntegerTextField valueField = getValueField();
        final int value = valueField.getValue();
        final Integer oldValue = getPropertyValue();

        changed(value, oldValue);
    }
}
