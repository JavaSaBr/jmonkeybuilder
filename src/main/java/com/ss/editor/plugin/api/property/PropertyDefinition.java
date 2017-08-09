package com.ss.editor.plugin.api.property;

import com.ss.editor.extension.property.EditablePropertyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The definition of a field of a creating object.
 *
 * @author JavaSaBr
 */
public final class PropertyDefinition {

    /**
     * The type of the property.
     */
    @NotNull
    private final EditablePropertyType propertyType;

    /**
     * The name of the property.
     */
    @NotNull
    private final String name;

    /**
     * The id of the property.
     */
    @NotNull
    private final String id;

    /**
     * The default value of the property.
     */
    @Nullable
    private final Object defaultValue;

    /**
     * The min value.
     */
    private final float min;

    /**
     * The max value.
     */
    private final float max;

    public PropertyDefinition(@NotNull final EditablePropertyType propertyType, @NotNull final String name,
                              @NotNull final String id, @Nullable final Object defaultValue) {
        this.propertyType = propertyType;
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
        this.max = Float.NaN;
        this.min = Float.NaN;
    }

    public PropertyDefinition(@NotNull final EditablePropertyType propertyType, @NotNull final String name,
                               @NotNull final String id, @Nullable final Object defaultValue, final float min,
                               final float max) {
        this.propertyType = propertyType;
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
    }

    /**
     * @return the type of the property.
     */
    @NotNull
    public EditablePropertyType getPropertyType() {
        return propertyType;
    }

    /**
     * @return the name of the property.
     */
    @Nullable
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return the id of the property.
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * @return the default value of the property.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @return the max value.
     */
    public float getMax() {
        return max;
    }

    /**
     * @return the min value.
     */
    public float getMin() {
        return min;
    }

    @Override
    public String toString() {
        return "PropertyDefinition{" + "propertyType=" + propertyType + ", name='" + name + '\'' + ", id='" + id +
                '\'' + ", defaultValue=" + defaultValue + '}';
    }
}
