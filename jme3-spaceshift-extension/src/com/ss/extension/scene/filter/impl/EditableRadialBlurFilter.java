package com.ss.extension.scene.filter.impl;

import com.jme3.post.filters.RadialBlurFilter;
import com.jme3.util.clone.Cloner;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;
import org.jetbrains.annotations.NotNull;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable implementation of radial blur filter.
 *
 * @author JavaSaBr
 */
public class EditableRadialBlurFilter extends RadialBlurFilter implements EditableSceneFilter<RadialBlurFilter> {

    @Override
    public RadialBlurFilter get() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return "Radial blur filter";
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

        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Sample distance", 0.1F, 0F, 100F, this,
                                        EditableRadialBlurFilter::getSampleDistance,
                                        EditableRadialBlurFilter::setSampleDistance));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Sample strength", 0.1F, 0F, 100F, this,
                                        EditableRadialBlurFilter::getSampleStrength,
                                        EditableRadialBlurFilter::setSampleStrength));

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
    }
}
