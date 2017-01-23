package com.ss.extension.scene.filter.impl;

import static com.ss.extension.loader.SceneLoader.tryToGetAssetManager;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.shadow.AbstractShadowFilter;
import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.util.clone.Cloner;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;
import com.ss.extension.scene.renderer.EditableDirectionalLightShadowRenderer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable implementation of a {@link DirectionalLightShadowFilter}.
 *
 * @author JavaSaBr
 */
public class EditableDirectionalLightShadowFilter extends AbstractShadowFilter<EditableDirectionalLightShadowRenderer>
        implements EditableSceneFilter<AbstractShadowFilter<?>> {

    public static final int SHADOW_MAP_SIZE = 1024;

    private static final Method SET_POST_MATERIAL_METHOD;

    static {
        try {
            SET_POST_MATERIAL_METHOD = AbstractShadowRenderer.class.getDeclaredMethod("setPostShadowMaterial", Material.class);
            SET_POST_MATERIAL_METHOD.setAccessible(true);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public EditableDirectionalLightShadowFilter() {
        this(tryToGetAssetManager(), SHADOW_MAP_SIZE, 3);
    }

    public EditableDirectionalLightShadowFilter(@NotNull final AssetManager assetManager, final int shadowMapSize, final int nbSplits) {
        super(assetManager, shadowMapSize, new EditableDirectionalLightShadowRenderer(assetManager, shadowMapSize, nbSplits));
    }

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {

        final Array<EditableProperty<?, ?>> result = ArrayFactory.newArray(EditableProperty.class);

        result.add(new SimpleProperty<>(EditablePropertyType.ENUM, "Edge filtering mode", this,
                EditableDirectionalLightShadowFilter::getEdgeFilteringMode, EditableDirectionalLightShadowFilter::setEdgeFilteringMode));
        result.add(new SimpleProperty<>(EditablePropertyType.ENUM, "Shadow compare mode", this,
                EditableDirectionalLightShadowFilter::getShadowCompareMode, EditableDirectionalLightShadowFilter::setShadowCompareMode));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Shadow z extend", this,
                EditableDirectionalLightShadowFilter::getShadowZExtend, EditableDirectionalLightShadowFilter::setShadowZExtend));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Shadow z fade length", this,
                EditableDirectionalLightShadowFilter::getShadowZFadeLength, EditableDirectionalLightShadowFilter::setShadowZFadeLength));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Lambda", this,
                EditableDirectionalLightShadowFilter::getLambda, EditableDirectionalLightShadowFilter::setLambda));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Shadow intensity", this,
                EditableDirectionalLightShadowFilter::getShadowIntensity, EditableDirectionalLightShadowFilter::setShadowIntensity));
        result.add(new SimpleProperty<>(EditablePropertyType.INTEGER, "Edges thickness", this,
                EditableDirectionalLightShadowFilter::getEdgesThickness, EditableDirectionalLightShadowFilter::setEdgesThickness));
        result.add(new SimpleProperty<>(EditablePropertyType.BOOLEAN, "Back faces shadows", this,
                EditableDirectionalLightShadowFilter::isRenderBackFacesShadows, EditableDirectionalLightShadowFilter::setRenderBackFacesShadows));
        result.add(new SimpleProperty<>(EditablePropertyType.BOOLEAN, "Stabilization", this,
                EditableDirectionalLightShadowFilter::isEnabledStabilization, EditableDirectionalLightShadowFilter::setEnabledStabilization));

        return result;
    }

    public void setLambda(final float lambda) {
        shadowRenderer.setLambda(lambda);
    }

    public float getLambda() {
        return shadowRenderer.getLambda();
    }

    public void setEnabledStabilization(final boolean stabilize) {
        shadowRenderer.setEnabledStabilization(stabilize);
    }

    public boolean isEnabledStabilization() {
        return shadowRenderer.isEnabledStabilization();
    }

    @Override
    public AbstractShadowFilter<?> get() {
        return this;
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        material = cloner.clone(material);
        shadowRenderer = cloner.clone(shadowRenderer);
        try {
            SET_POST_MATERIAL_METHOD.invoke(shadowRenderer, material);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(shadowRenderer, "shadowRenderer", null);

    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);
        final InputCapsule capsule = importer.getCapsule(this);
        shadowRenderer = (EditableDirectionalLightShadowRenderer) capsule.readSavable("shadowRenderer", null);
    }
}
