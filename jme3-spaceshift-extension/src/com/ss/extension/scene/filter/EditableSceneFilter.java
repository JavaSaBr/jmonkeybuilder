package com.ss.extension.scene.filter;

import com.jme3.post.Filter;
import com.ss.extension.property.EditableProperty;
import org.jetbrains.annotations.NotNull;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The interface to implement editing support.
 *
 * @author JavaSaBr
 */
public interface EditableSceneFilter<T extends Filter> extends SceneFilter<T> {

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
