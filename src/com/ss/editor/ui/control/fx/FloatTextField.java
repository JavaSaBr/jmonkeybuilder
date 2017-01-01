package com.ss.editor.ui.control.fx;

import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.util.converter.LimitedFloatStringConverter;

import org.jetbrains.annotations.NotNull;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.ScrollEvent;
import javafx.util.StringConverter;

/**
 * The implementation of a control for editing float values.
 *
 * @author JavaSaBr
 */
public final class FloatTextField extends TextField {

    /**
     * The scroll power.
     */
    private float scrollPower;

    public FloatTextField() {
        setTextFormatter(new TextFormatter<>(new LimitedFloatStringConverter()));
        setOnScroll(this::processScroll);
        setScrollPower(30);
    }

    public FloatTextField(@NotNull final String text) {
        super(text);
    }

    /**
     * @param scrollPower the scroll power.
     */
    public void setScrollPower(final float scrollPower) {
        this.scrollPower = scrollPower;
    }

    /**
     * @return the scroll power.
     */
    public float getScrollPower() {
        return scrollPower;
    }

    /**
     * Process of scrolling.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;
        event.consume();

        final float value = getValue();

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * getScrollPower();

        final float resultValue = longValue / 1000F;
        final String stringValue = String.valueOf(resultValue);

        final TextFormatter<?> textFormatter = getTextFormatter();
        final StringConverter<?> valueConverter = textFormatter.getValueConverter();
        try {
            valueConverter.fromString(stringValue);
        } catch (final RuntimeException e) {
            return;
        }

        setText(stringValue);
        positionCaret(stringValue.length());
    }

    /**
     * Add a new change listener.
     *
     * @param listener the change listener.
     */
    @FXThread
    public void addChangeListener(@NotNull final ChangeListener<Float> listener) {
        final TextFormatter<Float> textFormatter = unsafeCast(getTextFormatter());
        textFormatter.valueProperty().addListener(listener);
    }

    /**
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FXThread
    public void setMinMax(final float min, final float max) {
        final TextFormatter<Float> textFormatter = unsafeCast(getTextFormatter());
        final StringConverter<Float> valueConverter = textFormatter.getValueConverter();
        if (valueConverter instanceof LimitedFloatStringConverter) {
            final LimitedFloatStringConverter converter = (LimitedFloatStringConverter) valueConverter;
            converter.setMaxValue(max);
            converter.setMinValue(min);
        }
    }

    /**
     * Get a current value.
     *
     * @return the current value.
     */
    @FXThread
    public float getValue() {
        final TextFormatter<Float> textFormatter = unsafeCast(getTextFormatter());
        final Float value = textFormatter.getValue();
        return value == null ? 0F : value;
    }

    /**
     * Set a new value.
     *
     * @param value the new value.
     */
    @FXThread
    public void setValue(final float value) {
        setText(String.valueOf(value));
    }
}
