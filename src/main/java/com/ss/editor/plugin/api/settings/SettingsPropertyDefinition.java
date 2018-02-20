package com.ss.editor.plugin.api.settings;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.util.array.Array;
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

    public SettingsPropertyDefinition(@NotNull final EditablePropertyType propertyType, @NotNull final String name,
                                      @NotNull final String id, @NotNull final SettingsCategory category,
                                      @Nullable final Object defaultValue) {
        super(propertyType, name, id, defaultValue);
        this.category = category;
    }

    public SettingsPropertyDefinition(@NotNull final EditablePropertyType propertyType,
                                      @Nullable final Array<String> dependencies, @NotNull final String name,
                                      @NotNull final String id, @NotNull final SettingsCategory category,
                                      @Nullable final Object defaultValue) {
        super(propertyType, dependencies, name, id, defaultValue);
        this.category = category;
    }

    public SettingsPropertyDefinition(@NotNull final EditablePropertyType propertyType, @NotNull final String name,
                                      @NotNull final String id, @NotNull final SettingsCategory category,
                                      @Nullable final Object defaultValue, @Nullable final String extension) {
        super(propertyType, name, id, defaultValue, extension);
        this.category = category;
    }

    public SettingsPropertyDefinition(@NotNull final EditablePropertyType propertyType,
                                      @Nullable final Array<String> dependencies, @NotNull final String name,
                                      @NotNull final String id, @NotNull final SettingsCategory category,
                                      @Nullable final Object defaultValue, @Nullable final String extension) {
        super(propertyType, dependencies, name, id, defaultValue, extension);
        this.category = category;
    }

    public SettingsPropertyDefinition(@NotNull final EditablePropertyType propertyType, final @NotNull String name,
                                      @NotNull final String id, @NotNull final SettingsCategory category,
                                      @Nullable final Object defaultValue, @NotNull final Array<?> options) {
        super(propertyType, name, id, defaultValue, options);
        this.category = category;
    }

    public SettingsPropertyDefinition(@NotNull final EditablePropertyType propertyType, @NotNull final String name,
                                      @NotNull final String id, @NotNull final SettingsCategory category,
                                      @Nullable final Object defaultValue, final float min, final float max) {
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
