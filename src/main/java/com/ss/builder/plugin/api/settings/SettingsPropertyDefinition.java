package com.ss.builder.plugin.api.settings;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The class to define settings property.
 *
 * @author JavaSaBr
 */
public class SettingsPropertyDefinition extends PropertyDefinition {

    /**
     * The settings category.
     */
    @NotNull
    private final SettingsCategory category;

    public SettingsPropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull String name,
            @NotNull String id,
            @NotNull SettingsCategory category,
            @Nullable Object defaultValue
    ) {
        super(propertyType, name, id, defaultValue);
        this.category = category;
    }

    public SettingsPropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @Nullable Array<String> dependencies,
            @NotNull String name,
            @NotNull String id,
            @NotNull SettingsCategory category,
            @Nullable Object defaultValue
    ) {
        super(propertyType, dependencies, name, id, defaultValue);
        this.category = category;
    }

    public SettingsPropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull String name,
            @NotNull String id,
            @NotNull SettingsCategory category,
            @Nullable Object defaultValue,
            @Nullable String extension
    ) {
        super(propertyType, name, id, defaultValue, extension);
        this.category = category;
    }

    public SettingsPropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @Nullable Array<String> dependencies,
            @NotNull String name,
            @NotNull String id,
            @NotNull SettingsCategory category,
            @Nullable Object defaultValue,
            @Nullable String extension
    ) {
        super(propertyType, dependencies, name, id, defaultValue, extension);
        this.category = category;
    }

    public SettingsPropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull String name,
            @NotNull String id,
            @NotNull SettingsCategory category,
            @Nullable Object defaultValue,
            @NotNull Array<?> options
    ) {
        super(propertyType, name, id, defaultValue, options);
        this.category = category;
    }

    public SettingsPropertyDefinition(
            @NotNull EditablePropertyType propertyType,
            @NotNull String name,
            @NotNull String id,
            @NotNull SettingsCategory category,
            @Nullable Object defaultValue,
            float min,
            float max
    ) {
        super(propertyType, name, id, defaultValue, min, max);
        this.category = category;
    }

    /**
     * Get the settings category.
     *
     * @return the settings category.
     */
    @FromAnyThread
    public  @NotNull SettingsCategory getCategory() {
        return category;
    }
}
