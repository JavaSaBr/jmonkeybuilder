package com.ss.extension.scene.app.state.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface for implementing editable property of generic object.
 *
 * @author JavaSabr
 */
public interface EditableProperty<T, O> {

    /**
     * Get a name of this property.
     *
     * @return the name.
     */
    @NotNull
    String getName();

    /**
     * Get the current value.
     *
     * @return the current value.
     */
    @Nullable
    T getValue();

    /**
     * Get a type of this property.
     *
     * @return the property type.
     */
    @NotNull
    EditablePropertyType getType();

    /**
     * @return scroll power.
     */
    default float getScrollPower() {
        return 1F;
    }

    /**
     * @return the min value.
     */
    default float getMinValue() {
        return Integer.MIN_VALUE;
    }

    /**
     * @return the max value.
     */
    default float getMaxValue() {
        return Integer.MAX_VALUE;
    }

    /**
     * Get an edited object.
     *
     * @return the edited object.
     */
    @NotNull
    O getObject();

    /**
     * Set a new value.
     *
     * @param value the new value.
     */
    void setValue(@Nullable final T value);
}
