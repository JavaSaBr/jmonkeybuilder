package com.ss.editor.plugin.api.property;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.rlib.common.util.ObjectUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The definition of a field of a creating object.
 *
 * @author JavaSaBr
 */
public class PropertyDefinition {

    private static final Array<Object> EMPTY_OPTIONS = ArrayFactory.asArray();
    private static final Array<String> EMPTY_DEPENDENCIES = ArrayFactory.newArray(String.class);

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
     * The list of available options of this property.
     */
    @NotNull
    private final Array<?> options;

    /**
     * The dependencies.
     */
    @NotNull
    private final Array<String> dependencies;

    /**
     * The file extension to filter files/resources.
     */
    @Nullable
    private final String extension;

    /**
     * The min value.
     */
    private final float min;

    /**
     * The max value.
     */
    private final float max;

    public PropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull String name,
            @NotNull String id,
            @Nullable Object defaultValue
    ) {
        this(propertyType, null, name, id, defaultValue);
    }

    public PropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @Nullable Array<String> dependencies,
            @NotNull String name,
            @NotNull String id,
            @Nullable Object defaultValue
    ) {
        this.propertyType = propertyType;
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
        this.max = Float.NaN;
        this.min = Float.NaN;
        this.options = EMPTY_OPTIONS;
        this.extension = null;
        this.dependencies = dependencies == null ? EMPTY_DEPENDENCIES : dependencies;
    }

    public PropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull String name,
            @NotNull String id,
            @Nullable Object defaultValue,
            @Nullable String extension
    ) {
        this(propertyType, null, name, id, defaultValue, extension);
    }

    public PropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @Nullable Array<String> dependencies,
            @NotNull String name,
            @NotNull String id,
            @Nullable Object defaultValue,
            @Nullable String extension
    ) {
        this.propertyType = propertyType;
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
        this.max = Float.NaN;
        this.min = Float.NaN;
        this.options = EMPTY_OPTIONS;
        this.extension = extension;
        this.dependencies = dependencies == null ? EMPTY_DEPENDENCIES : dependencies;
    }

    public PropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull final String name,
            @NotNull String id,
            @Nullable Object defaultValue,
            @NotNull Array<?> options
    ) {
        this.propertyType = propertyType;
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
        this.options = options;
        this.max = Float.NaN;
        this.min = Float.NaN;
        this.extension = null;
        this.dependencies = EMPTY_DEPENDENCIES;
    }

    public PropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull String name,
            @NotNull String id,
            @Nullable Object defaultValue,
            float min,
            float max
    ) {
        this.propertyType = propertyType;
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.options = EMPTY_OPTIONS;
        this.extension = null;
        this.dependencies = EMPTY_DEPENDENCIES;
    }

    /**
     * Get the type of the property.
     *
     * @return the type of the property.
     */
    @FromAnyThread
    public @NotNull EditablePropertyType getPropertyType() {
        return propertyType;
    }

    /**
     * Get the default value of the property.
     *
     * @return the default value of the property or null.
     */
    @FromAnyThread
    public @Nullable Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the default value of the property.
     *
     * @return the default value of the property.
     */
    @FromAnyThread
    public @NotNull Object requireDefaultValue() {
        return ObjectUtils.notNull(defaultValue);
    }

    /**
     * Get the dependencies.
     *
     * @return the dependencies.
     */
    @FromAnyThread
    public @NotNull Array<String> getDependencies() {
        return dependencies;
    }

    /**
     * Get the id of the property.
     *
     * @return the id of the property.
     */
    @FromAnyThread
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get the default value of the property.
     *
     * @return the default value of the property.
     */
    @FromAnyThread
    public @NotNull String getName() {
        return name;
    }

    /**
     * Get the max value.
     *
     * @return the max value.
     */
    @FromAnyThread
    public float getMax() {
        return max;
    }

    /**
     * Get the min value.
     *
     * @return the min value.
     */
    @FromAnyThread
    public float getMin() {
        return min;
    }

    /**
     * Get the options.
     *
     * @return the options.
     */
    @FromAnyThread
    public @NotNull Array<?> getOptions() {
        return options;
    }

    /**
     * Get the file extension to filter files/resources.
     *
     * @return the file extension to filter files/resources.
     */
    @FromAnyThread
    public @Nullable String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return "PropertyDefinition{" + "propertyType=" + propertyType + ", name='" + name + '\'' + ", id='" + id +
                '\'' + ", defaultValue=" + defaultValue + ", options=" + options + ", extension='" + extension + '\'' +
                ", min=" + min + ", max=" + max + '}';
    }
}
