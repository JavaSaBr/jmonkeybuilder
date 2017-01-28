package com.ss.extension.scene.filter.impl;

import com.jme3.post.filters.BloomFilter;
import com.jme3.util.clone.Cloner;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;
import org.jetbrains.annotations.NotNull;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable implementation of bloom filter.
 *
 * @author JavaSaBr
 */
public class EditableBloomFilter extends BloomFilter implements EditableSceneFilter<BloomFilter> {

    public EditableBloomFilter(@NotNull final GlowMode glowMode) {
        super(glowMode);
    }

    @Override
    public BloomFilter get() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return "Bloom filter";
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

        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Blur scale", 0.1F, 0F, 10F, this,
                                        EditableBloomFilter::getBlurScale,
                                        EditableBloomFilter::setBlurScale));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Bloom intensity", 0.1F, 0F, 10F, this,
                                        EditableBloomFilter::getBloomIntensity,
                                        EditableBloomFilter::setBloomIntensity));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Exposure cut off", 0.01F, 0F, 100F, this,
                                        EditableBloomFilter::getExposureCutOff,
                                        EditableBloomFilter::setExposureCutOff));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Exposure power", 0.1F, 0F, 100F, this,
                                        EditableBloomFilter::getExposurePower,
                                        EditableBloomFilter::setExposurePower));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Down sampling factor", 0.1F, 0F, 100F, this,
                                        EditableBloomFilter::getDownSamplingFactor,
                                        EditableBloomFilter::setDownSamplingFactor));

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
    }
}
