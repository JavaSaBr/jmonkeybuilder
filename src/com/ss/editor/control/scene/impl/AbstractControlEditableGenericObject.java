package com.ss.editor.control.scene.impl;

import com.ss.editor.ui.control.model.property.generic.EditableGenericObject;
import com.ss.editor.ui.control.model.property.generic.EditableProperty;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The base implementation of an editing generic object for the default jME controls.
 *
 * @author JavaSaBr
 */
public abstract class AbstractControlEditableGenericObject<C> implements EditableGenericObject {

    /**
     * The control.
     */
    @NotNull
    private final C control;

    public AbstractControlEditableGenericObject(final @NotNull C control) {
        this.control = control;
    }

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {
        final Array<EditableProperty<?, ?>> properties = ArrayFactory.newArray(EditableProperty.class);
        fillEditableProperties(control, properties);
        return properties;
    }

    /**
     * Get a list of editable properties.
     *
     * @param control the control.
     */
    protected abstract void fillEditableProperties(@NotNull final C control,
                                                   @NotNull final Array<EditableProperty<?, ?>> properties);
}
