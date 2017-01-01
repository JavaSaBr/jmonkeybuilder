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
    private int minValue;

    /**
     * The max value.
     */
    private int maxValue;

    public LimitedIntegerStringConverter() {
        this.maxValue = Integer.MAX_VALUE;
        this.minValue = Integer.MIN_VALUE;
    }

    @Nullable
    @Override
    public Integer fromString(@Nullable final String value) {
        if (StringUtils.isEmpty(value)) return null;

        final Integer result = Integer.valueOf(value);

        if (result < minValue) {
            return minValue;
        } else if (result > maxValue) {
            return maxValue;
        }

        return result;
    }

    /**
     * @return the max value.
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the max value.
     */
    public void setMaxValue(final int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * @return the min value.
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the min value.
     */
    public void setMinValue(final int minValue) {
        this.minValue = minValue;
    }

    @Nullable
    @Override
    public String toString(@Nullable final Integer value) {
        if (value == null) return StringUtils.EMPTY;
        return Integer.toString(value.intValue());
    }
}
