package com.ss.extension.scene.filter.impl;

import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.ss.extension.property.EditableProperty;
import com.ss.extension.property.EditablePropertyType;
import com.ss.extension.property.SimpleProperty;
import org.jetbrains.annotations.NotNull;
import rlib.util.array.Array;

import java.util.logging.Level;

/**
 * The editable implementation of a {@link DirectionalLightShadowFilter} which uses the light from a scene.
 *
 * @author JavaSaBr
 */
public class EditableDirectionLightFromSceneShadowFilter extends EditableDirectionalLightShadowFilter {

    static {
        java.util.logging.Logger.getLogger(AbstractShadowRenderer.class.getName())
                                .setLevel(Level.OFF);
    }

    public EditableDirectionLightFromSceneShadowFilter() {
    }

    @NotNull
    @Override
    public String getName() {
        return "Shadows from direction light";
    }

    @NotNull
    @Override
    public Array<EditableProperty<?, ?>> getEditableProperties() {

        final Array<EditableProperty<?, ?>> result = super.getEditableProperties();
        result.add(new SimpleProperty<>(EditablePropertyType.DIRECTION_LIGHT_FROM_SCENE, "Direction light", this,
                                        EditableDirectionalLightShadowFilter::getLight,
                                        EditableDirectionalLightShadowFilter::setLight));

        return result;
    }
}
