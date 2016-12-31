package com.ss.editor.ui.util.converter;

import org.jetbrains.annotations.Nullable;

import javafx.util.StringConverter;
import rlib.util.StringUtils;

/**
 * The implementation of limited integer string converter.
 *
 * @author JavaSaBR
 */
public class LimitedIntegerStringConverter extends StringConverter<Integer> {

    /**
     * The min value.
     */
    private final int minValue;

    /**
     * The max value.
     */
    private final int maxValue;

    public LimitedIntegerStringConverter(final int minValue, final int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Nullable
    @Override
    public Integer fromString(@Nullable final String value) {
        if (StringUtils.isEmpty(value)) return null;

        final Integer result = Integer.valueOf(value);

        if (result < minValue) {
            throw new RuntimeException("The value " + value + " is less than min value " + minValue);
        } else if (result > maxValue) {
            throw new RuntimeException("The value " + value + " is great than max value " + maxValue);
        }

        return result;
    }

    @Nullable
    @Override
    public String toString(@Nullable final Integer value) {
        if (value == null) return StringUtils.EMPTY;
        return Integer.toString(value.intValue());
    }
}
