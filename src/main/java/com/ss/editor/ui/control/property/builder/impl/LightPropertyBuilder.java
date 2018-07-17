package com.ss.editor.ui.control.property.builder.impl;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.SeparatorProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.ss.editor.extension.property.EditablePropertyType.*;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Light} objects.
 *
 * @author JavaSaBr
 */
public class LightPropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final PropertyBuilder INSTANCE = new LightPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private LightPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!(object instanceof Light)) {
            return null;
        }

        var light = (Light) object;
        var properties = new ArrayList<EditableProperty<?, ?>>();

        if (light instanceof DirectionalLight) {

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_DIRECTION, (DirectionalLight) light,
                    DirectionalLight::getDirection, DirectionalLight::setDirection));

            properties.add(SeparatorProperty.getInstance());

        } else if (light instanceof SpotLight) {

            var spotLight = (SpotLight) light;

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_DIRECTION, spotLight,
                    SpotLight::getDirection, SpotLight::setDirection));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_LOCATION, spotLight,
                    SpotLight::getPosition, SpotLight::setPosition));

            properties.add(SeparatorProperty.getInstance());

            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_RADIUS, 1F, 0F, Integer.MAX_VALUE, spotLight,
                    SpotLight::getSpotRange, SpotLight::setSpotRange));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_INNER_ANGLE, 1F, 0F, FastMath.HALF_PI, spotLight,
                    SpotLight::getSpotInnerAngle, SpotLight::setSpotInnerAngle));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_OUTER_ANGLE, 1F, 0F, FastMath.HALF_PI, spotLight,
                    SpotLight::getSpotOuterAngle, SpotLight::setSpotOuterAngle));

        } else if (light instanceof PointLight) {

            var pointLight = (PointLight) light;

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_LOCATION, pointLight,
                PointLight::getPosition, PointLight::setPosition));

            properties.add(SeparatorProperty.getInstance());

            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_RADIUS, 1F, 0F, Integer.MAX_VALUE, pointLight,
                PointLight::getRadius, PointLight::setRadius));
        }

        properties.add(new SimpleProperty<>(COLOR, Messages.MODEL_PROPERTY_COLOR, light,
                Light::getColor, Light::setColor));

        return properties;
    }
}
