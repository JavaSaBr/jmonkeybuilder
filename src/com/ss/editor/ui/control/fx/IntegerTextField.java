package com.ss.editor.ui.control.fx;

import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.ui.util.converter.LimitedIntegerStringConverter;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.ScrollEvent;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * The implementation of a control for editing integer values.
 *
 * @author JavaSaBr
 */
public final class IntegerTextField extends TextField {

    public static final IntegerStringConverter STRING_CONVERTER = new IntegerStringConverter();

    /**
     * The scroll power.
     */
    private int scrollPower;

    public IntegerTextField() {
        setTextFormatter(new TextFormatter<>(STRING_CONVERTER));
        setOnScroll(this::processScroll);
        setScrollPower(30);
    }

    public IntegerTextField(@NotNull final String text) {
        super(text);
    }

    /**
     * @param scrollPower the scroll power.
     */
    public void setScrollPower(final int scrollPower) {
        this.scrollPower = scrollPower;
    }

    /**
     * @return the scroll power.
     */
    public int getScrollPower() {
        return scrollPower;
    }

    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final int value = getValue();

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * getScrollPower();

        final int resultValue = (int) (longValue / 1000);
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
    public void setMinMax(final int min, final int max) {
        setTextFormatter(new TextFormatter<>(new LimitedIntegerStringConverter(min, max)));
    }

    /**
     * Get a current value.
     *
     * @return the current value.
     */
    public int getValue() {
        final TextFormatter<Integer> textFormatter = unsafeCast(getTextFormatter());
        return textFormatter.getValue();
    }

    /**
     * Set a new value.
     *
     * @param value the new value.
     */
    public void setValue(final int value) {
        setText(String.valueOf(value));
    }
}
