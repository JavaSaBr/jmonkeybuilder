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
    private final float minValue;

    /**
     * The max value.
     */
    private final float maxValue;

    public LimitedFloatStringConverter(final float minValue, final float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Nullable
    @Override
    public Float fromString(@Nullable final String value) {
        if (StringUtils.isEmpty(value)) return null;

        final Float result = Float.valueOf(value);

        if (result < minValue) {
            throw new RuntimeException("The value " + value + " is less than min value " + minValue);
        } else if (result > maxValue) {
            throw new RuntimeException("The value " + value + " is great than max value " + maxValue);
        }

        return result;
    }

    @Nullable
    @Override
    public String toString(@Nullable final Float value) {
        if (value == null) return StringUtils.EMPTY;
        return Float.toString(value.floatValue());
    }
}
