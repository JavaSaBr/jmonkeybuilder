package com.ss.extension.scene.filter.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.post.filters.CartoonEdgeFilter;
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
 * The editable implementation of cartoon edge filter.
 *
 * @author JavaSaBr
 */
public class EditableCartoonEdgeFilter extends CartoonEdgeFilter implements EditableSceneFilter<CartoonEdgeFilter> {

    @Override
    public CartoonEdgeFilter get() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return "Cartoon edge filter";
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

        result.add(new SimpleProperty<>(EditablePropertyType.COLOR, "Edge color", this,
                                        EditableCartoonEdgeFilter::getEdgeColor,
                                        EditableCartoonEdgeFilter::setEdgeColor));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Edge width", this,
                                        EditableCartoonEdgeFilter::getEdgeWidth,
                                        EditableCartoonEdgeFilter::setEdgeWidth));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Edge intensity", this,
                                        EditableCartoonEdgeFilter::getEdgeIntensity,
                                        EditableCartoonEdgeFilter::setEdgeIntensity));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Normal threshold", this,
                                        EditableCartoonEdgeFilter::getNormalThreshold,
                                        EditableCartoonEdgeFilter::setNormalThreshold));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Depth threshold", this,
                                        EditableCartoonEdgeFilter::getDepthThreshold,
                                        EditableCartoonEdgeFilter::setDepthThreshold));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Normal sensitivity", this,
                                        EditableCartoonEdgeFilter::getNormalSensitivity,
                                        EditableCartoonEdgeFilter::setNormalSensitivity));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Depth sensitivity", this,
                                        EditableCartoonEdgeFilter::getDepthSensitivity,
                                        EditableCartoonEdgeFilter::setDepthSensitivity));

        return result;
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        setEdgeColor(cloner.clone(getEdgeColor()));
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);
        final InputCapsule capsule = importer.getCapsule(this);
        setEdgeWidth(capsule.readFloat("edgeWidth", 1.0f));
        setEdgeIntensity(capsule.readFloat("edgeIntensity", 1.0f));
        setNormalThreshold(capsule.readFloat("normalThreshold", 0.5f));
        setDepthThreshold(capsule.readFloat("depthThreshold", 0.1f));
        setNormalSensitivity(capsule.readFloat("normalSensitivity", 1.0f));
        setDepthSensitivity(capsule.readFloat("depthSensitivity", 10.0f));
        setEdgeColor((ColorRGBA) capsule.readSavable("edgeColor", new ColorRGBA(0, 0, 0, 1)));
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(getEdgeWidth(), "edgeWidth", 1.0f);
        capsule.write(getEdgeIntensity(), "edgeIntensity", 1.0f);
        capsule.write(getNormalThreshold(), "normalThreshold", 0.5f);
        capsule.write(getDepthThreshold(), "depthThreshold", 0.1f);
        capsule.write(getNormalSensitivity(), "normalSensitivity", 1.0f);
        capsule.write(getDepthSensitivity(), "depthSensitivity", 10.0f);
        capsule.write(getEdgeColor(), "edgeColor", new ColorRGBA(0, 0, 0, 1));
    }
}
