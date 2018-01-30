package com.ss.editor.ui.component.painting.property;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The definition of property to {@link com.ss.editor.ui.component.painting.PaintingComponent}.
 *
 * @author JavaSaBr
 */
public class PaintingPropertyDefinition extends PropertyDefinition {

    @NotNull
    private final String category;

    public PaintingPropertyDefinition(@NotNull final String category, @NotNull final EditablePropertyType propertyType,
                                      @NotNull final String name, @NotNull final String id,
                                      @Nullable final Object defaultValue) {
        super(propertyType, name, id, defaultValue);
        this.category = category;
    }

    /**
     * Get the category.
     *
     * @return the category.
     */
    @FromAnyThread
    public @NotNull String getCategory() {
        return category;
    }
}
