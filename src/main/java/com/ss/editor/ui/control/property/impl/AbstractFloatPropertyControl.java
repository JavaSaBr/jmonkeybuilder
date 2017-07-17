package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit float values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractFloatPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, Float> {

    /**
     * The filed with current value.
     */
    @Nullable
    private FloatTextField valueField;

    /**
     * Instantiates a new Abstract float property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractFloatPropertyControl(@Nullable final Float propertyValue, @NotNull final String propertyName,
                                        @NotNull final C changeConsumer,
                                        @NotNull final SixObjectConsumer<C, T, String, Float, Float, BiConsumer<T, Float>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new FloatTextField();
        valueField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        valueField.addChangeListener((observable, oldValue, newValue) -> updateValue());
        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, container);
    }

    /**
     * Sets scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FXThread
    public void setScrollPower(final float scrollPower) {
        getValueField().setScrollPower(scrollPower);
    }

    /**
     * Gets scroll power.
     *
     * @return the scroll power.
     */
    @FXThread
    public float getScrollPower() {
        return getValueField().getScrollPower();
    }

    /**
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FXThread
    public void setMinMax(final float min, final float max) {
        getValueField().setMinMax(min, max);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the filed with current value.
     */
    @NotNull
    private FloatTextField getValueField() {
        return notNull(valueField);
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
