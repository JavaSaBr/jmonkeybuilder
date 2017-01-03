package com.ss.editor.ui.util.converter;

import org.jetbrains.annotations.Nullable;

import javafx.util.StringConverter;
import rlib.util.StringUtils;

/**
 * The implementation of limited float string converter.
 *
 * @author JavaSaBR
 */
public class LimitedFloatStringConverter extends StringConverter<Float> {

    /**
     * The min value.
     */
    private float minValue;

    /**
     * The max value.
     */
    private float maxValue;

    public LimitedFloatStringConverter() {
        this.maxValue = Integer.MAX_VALUE;
        this.minValue = Integer.MIN_VALUE;
    }

    @Nullable
    @Override
    public Float fromString(@Nullable final String value) {
        if (StringUtils.isEmpty(value)) return null;

        final Float result = Float.valueOf(value);

        if (result < minValue) {
            throw new IllegalArgumentException();
        } else if (result > maxValue) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    /**
     * @return the max value.
     */
    public float getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the max value.
     */
    public void setMaxValue(final float maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * @return the min value.
     */
    public float getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the min value.
     */
    public void setMinValue(final float minValue) {
        this.minValue = minValue;
    }

    @Nullable
    @Override
    public String toString(@Nullable final Float value) {
        if (value == null) return StringUtils.EMPTY;
        return Float.toString(value.floatValue());
    }
}
