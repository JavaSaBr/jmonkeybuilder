package com.ss.extension.scene.filter.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.post.filters.PosterizationFilter;
import com.jme3.util.clone.Cloner;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;
import org.jetbrains.annotations.NotNull;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import java.io.IOException;

/**
 * The editable implementation of posterization filter.
 *
 * @author JavaSaBr
 */
public class EditablePosterizationFilter extends PosterizationFilter implements
        EditableSceneFilter<PosterizationFilter> {

    @Override
    public PosterizationFilter get() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return "Posterization filter";
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

        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Gamma", 0.005F, 0F, 10F, this,
                                        EditablePosterizationFilter::getGamma,
                                        EditablePosterizationFilter::setGamma));
        result.add(new SimpleProperty<>(EditablePropertyType.INTEGER, "Num colors", 1F, 0F, 100F, this,
                                        EditablePosterizationFilter::getNumColors,
                                        EditablePosterizationFilter::setNumColors));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Strength", 0.1F, 0F, 100F, this,
                                        EditablePosterizationFilter::getStrength,
                                        EditablePosterizationFilter::setStrength));

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);
        final InputCapsule capsule = importer.getCapsule(this);
        setGamma(capsule.readFloat("gamma", 0.6F));
        setNumColors(capsule.readInt("numColors", 8));
        setStrength(capsule.readFloat("strength", 1.0f));
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(getGamma(), "gamma", 0.6F);
        capsule.write(getNumColors(), "numColors", 8);
        capsule.write(getStrength(), "strength", 1.0f);
    }
}
