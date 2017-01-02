package com.ss.editor.ui.control.fx;

import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.util.converter.LimitedIntegerStringConverter;

import org.jetbrains.annotations.NotNull;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.ScrollEvent;
import javafx.util.StringConverter;

/**
 * The implementation of a control for editing integer values.
 *
 * @author JavaSaBr
 */
public final class IntegerTextField extends TextField {

    /**
     * The scroll power.
     */
    private int scrollPower;

    public IntegerTextField() {
        setTextFormatter(new TextFormatter<>(new LimitedIntegerStringConverter()));
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

    /**
     * Process of scrolling.
     */
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
     * Add a new change listener.
     *
     * @param listener the change listener.
     */
    @FXThread
    public void addChangeListener(@NotNull final ChangeListener<Integer> listener) {
        final TextFormatter<Integer> textFormatter = unsafeCast(getTextFormatter());
        textFormatter.valueProperty().addListener(listener);
    }

    /**
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max thr max value.
     */
    public void setMinMax(final int min, final int max) {
        final TextFormatter<Integer> textFormatter = unsafeCast(getTextFormatter());
        final StringConverter<Integer> valueConverter = textFormatter.getValueConverter();
        if (valueConverter instanceof LimitedIntegerStringConverter) {
            final LimitedIntegerStringConverter converter = (LimitedIntegerStringConverter) valueConverter;
            converter.setMaxValue(max);
            converter.setMinValue(min);
        }
    }

    /**
     * Get a current value.
     *
     * @return the current value.
     */
    public int getValue() {

        final TextFormatter<Integer> textFormatter = unsafeCast(getTextFormatter());
        final StringConverter<Integer> valueConverter = textFormatter.getValueConverter();
        final Integer value = textFormatter.getValue();

        if (value == null && valueConverter instanceof LimitedIntegerStringConverter) {
            return ((LimitedIntegerStringConverter) valueConverter).getMinValue();
        }

        return value == null ? 0 : value;
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
