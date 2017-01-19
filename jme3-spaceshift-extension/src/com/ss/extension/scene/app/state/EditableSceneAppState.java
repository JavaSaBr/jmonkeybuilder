package com.ss.extension.scene.app.state;

import com.ss.extension.scene.app.state.property.EditableProperty;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The interface to implement am editable scene app state.
 *
 * @author JavaSaBr
 */
public interface EditableSceneAppState extends SceneAppState {

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

