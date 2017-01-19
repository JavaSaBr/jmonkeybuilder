package com.ss.editor.ui.control.model.property.generic;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;

/**
 * The interface for implementing editable generic object.
 *
 * @author JavaSaBr
 */
public interface EditableGenericObject {

    /**
     * Get list of editable properties.
     *
     * @return the list of editable properties.
     */
    @NotNull
    Array<EditableProperty<?, ?>> getEditableProperties();
}
