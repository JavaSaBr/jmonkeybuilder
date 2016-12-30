package com.ss.editor.ui.control.model.property.builder.impl.generic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface for implementing a factory to get an editable generic object.
 *
 * @author JavaSaBr
 */
public interface EditableGenericObjectFactory {

    /**
     * Get an editable object from an object.
     *
     * @param object the object.
     * @return an editable object or null.
     */
    @Nullable
    EditableGenericObject make(@NotNull final Object object);
}
