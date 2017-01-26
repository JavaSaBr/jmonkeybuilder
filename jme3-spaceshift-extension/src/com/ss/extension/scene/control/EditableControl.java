package com.ss.extension.scene.control;

import com.jme3.scene.control.Control;
import com.ss.extension.property.EditableProperty;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The interface to implement an editable control.
 *
 * @author JavaSaBr
 */
public interface EditableControl extends Control {

    Array<EditableProperty<?, ?>> EMPTY_PROPERTIES = ArrayFactory.newArray(EditableProperty.class);

    /**
     * Get list of editable properties.
     *
     * @return the list of editable properties.
     */
    @NotNull
    default Array<EditableProperty<?, ?>> getEditableProperties() {
        return EMPTY_PROPERTIES;
    }
}
