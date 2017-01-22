package com.ss.extension.scene.filter.impl;

import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.util.clone.Cloner;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable implementation of depth of field filter.
 *
 * @author JavaSaBr
 */
public class EditableDepthOfFieldFilter extends DepthOfFieldFilter implements EditableSceneFilter<DepthOfFieldFilter> {

    @Override
    public DepthOfFieldFilter get() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return "Depth of field filter";
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {

        final Array<EditableProperty<?, ?>> result = ArrayFactory.newArray(EditableProperty.class);

        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Blur scale", 0.01F, 0F, 100F, this,
                DepthOfFieldFilter::getBlurScale, DepthOfFieldFilter::setBlurScale));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Focus distance", 1F, 0F, Integer.MAX_VALUE, this,
                DepthOfFieldFilter::getFocusDistance, DepthOfFieldFilter::setFocusDistance));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Focus range", 1F, 0F, Integer.MAX_VALUE, this,
                DepthOfFieldFilter::getFocusRange, DepthOfFieldFilter::setFocusRange));

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
    }
}
