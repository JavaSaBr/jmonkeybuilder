package com.ss.extension.scene.filter.impl;

import static com.ss.extension.loader.SceneLoader.tryToGetAssetManager;
import com.jme3.shadow.AbstractShadowFilter;
import com.jme3.shadow.PointLightShadowFilter;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import com.ss.extension.scene.filter.EditableSceneFilter;
import org.jetbrains.annotations.NotNull;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The editable implementation of a {@link PointLightShadowFilter}.
 *
 * @author JavaSaBr
 */
@SuppressWarnings("WeakerAccess")
public class EditablePointLightShadowFilter extends PointLightShadowFilter implements
        EditableSceneFilter<AbstractShadowFilter<?>> {

    public static final int SHADOW_MAP_SIZE = 1024;

    public EditablePointLightShadowFilter() {
        super(tryToGetAssetManager(), SHADOW_MAP_SIZE);
    }

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {

        final Array<EditableProperty<?, ?>> result = ArrayFactory.newArray(EditableProperty.class);

        result.add(new SimpleProperty<>(EditablePropertyType.ENUM, "Edge filtering mode", this,
                                        EditablePointLightShadowFilter::getEdgeFilteringMode,
                                        EditablePointLightShadowFilter::setEdgeFilteringMode));
        result.add(new SimpleProperty<>(EditablePropertyType.ENUM, "Shadow compare mode", this,
                                        EditablePointLightShadowFilter::getShadowCompareMode,
                                        EditablePointLightShadowFilter::setShadowCompareMode));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Shadow z extend", this,
                                        EditablePointLightShadowFilter::getShadowZExtend,
                                        EditablePointLightShadowFilter::setShadowZExtend));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Shadow z fade length", this,
                                        EditablePointLightShadowFilter::getShadowZFadeLength,
                                        EditablePointLightShadowFilter::setShadowZFadeLength));
        result.add(new SimpleProperty<>(EditablePropertyType.FLOAT, "Shadow intensity", this,
                                        EditablePointLightShadowFilter::getShadowIntensity,
                                        EditablePointLightShadowFilter::setShadowIntensity));
        result.add(new SimpleProperty<>(EditablePropertyType.INTEGER, "Edges thickness", this,
                                        EditablePointLightShadowFilter::getEdgesThickness,
                                        EditablePointLightShadowFilter::setEdgesThickness));
        result.add(new SimpleProperty<>(EditablePropertyType.BOOLEAN, "Back faces shadows", this,
                                        EditablePointLightShadowFilter::isRenderBackFacesShadows,
                                        EditablePointLightShadowFilter::setRenderBackFacesShadows));

        return result;
    }

    @Override
    public AbstractShadowFilter<?> get() {
        return this;
    }
}
