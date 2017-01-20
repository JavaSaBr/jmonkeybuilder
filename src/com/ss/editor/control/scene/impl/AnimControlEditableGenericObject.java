package com.ss.editor.control.scene.impl;

import com.jme3.animation.AnimControl;
import com.ss.editor.ui.control.model.property.generic.EditableProperty;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;

/**
 * The implementation of an editable generic object for editing {@link AnimControl}.
 *
 * @author JavaSaBr
 */
public class AnimControlEditableGenericObject extends AbstractControlEditableGenericObject<AnimControl> {

    public AnimControlEditableGenericObject(final @NotNull AnimControl control) {
        super(control);
    }

    @Override
    protected void fillEditableProperties(@NotNull final AnimControl control,
                                          @NotNull final Array<EditableProperty<?, ?>> properties) {

    }
}
