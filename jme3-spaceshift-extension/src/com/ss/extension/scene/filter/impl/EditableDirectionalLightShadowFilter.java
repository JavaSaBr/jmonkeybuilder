package com.ss.extension.scene.filter.impl;

import static com.ss.extension.loader.SceneLoader.tryToGetAssetManager;

import com.jme3.shadow.AbstractShadowFilter;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable implementation of a {@link DirectionalLightShadowFilter}.
 *
 * @author JavaSaBr
 */
public class EditableDirectionalLightShadowFilter extends DirectionalLightShadowFilter
        implements EditableSceneFilter<AbstractShadowFilter<?>> {

    public static final int SHADOW_MAP_SIZE = 1024;

    public EditableDirectionalLightShadowFilter() {
        super(tryToGetAssetManager(), SHADOW_MAP_SIZE, 3);
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
}
