package com.ss.editor.ui.control.fx;

import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.ui.util.converter.LimitedFloatStringConverter;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.ScrollEvent;
import javafx.util.StringConverter;
import javafx.util.converter.FloatStringConverter;

/**
 * The implementation of a control for editing float values.
 *
 * @author JavaSaBr
 */
public final class FloatTextField extends TextField {

    public static final FloatStringConverter STRING_CONVERTER = new FloatStringConverter();

    /**
     * The scroll power.
     */
    private float scrollPower;

    public FloatTextField() {
        setTextFormatter(new TextFormatter<>(STRING_CONVERTER));
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

    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

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
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max thr max value.
     */
    public void setMinMax(final float min, final float max) {
        setTextFormatter(new TextFormatter<>(new LimitedFloatStringConverter(min, max)));
    }

    /**
     * Get a current value.
     *
     * @return the current value.
     */
    public float getValue() {
        final TextFormatter<Float> textFormatter = unsafeCast(getTextFormatter());
        return textFormatter.getValue();
    }

    /**
     * Set a new value.
     *
     * @param value the new value.
     */
    public void setValue(final float value) {
        setText(String.valueOf(value));
    }
}
