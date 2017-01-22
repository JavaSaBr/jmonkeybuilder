package com.ss.extension.scene.filter.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.util.clone.Cloner;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable implementation of FXAA filter.
 *
 * @author JavaSaBr
 */
public class EditableFXAAFilter extends FXAAFilter implements EditableSceneFilter<FXAAFilter> {

    @Override
    public FXAAFilter get() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return "FXAA filter";
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

        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Sub pixel shift", 0.005F, 0F, 10F, this,
                EditableFXAAFilter::getSubPixelShift, EditableFXAAFilter::setSubPixelShift));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "VX offset", 0.005F, 0F, 10F, this,
                EditableFXAAFilter::getVxOffset, EditableFXAAFilter::setVxOffset));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Span max", 1F, 0F, 100F, this,
                EditableFXAAFilter::getSpanMax, EditableFXAAFilter::setSpanMax));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Reduce mul", 0.1F, 0F, 100F, this,
                EditableFXAAFilter::getReduceMul, EditableFXAAFilter::setReduceMul));

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);
        final InputCapsule capsule = importer.getCapsule(this);
        setSubPixelShift(capsule.readFloat("subPixelShift", 1.0f / 4.0f));
        setVxOffset(capsule.readFloat("vxOffset", 0.0f));
        setSpanMax(capsule.readFloat("spanMax", 8.0f));
        setReduceMul(capsule.readFloat("reduceMul", 1.0f / 8.0f));
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(getSubPixelShift(), "subPixelShift", 1.0f / 4.0f);
        capsule.write(getVxOffset(), "vxOffset", 0.0f);
        capsule.write(getSpanMax(), "spanMax", 8.0f);
        capsule.write(getReduceMul(), "reduceMul", 1.0f / 8.0f);
    }
}
