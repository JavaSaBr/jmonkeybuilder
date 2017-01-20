package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.ColorLightPropertyControl;
import com.ss.editor.ui.control.model.property.control.DirectionLightPropertyControl;
import com.ss.editor.ui.control.model.property.control.FloatLightPropertyControl;
import com.ss.editor.ui.control.model.property.control.PositionLightPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link Light} objects.
 *
 * @author JavaSaBr
 */
public class LightPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    private static final BiConsumer<PointLight, Float> POINT_LIGHT_RADIUS_APPLY_HANDLER = (pointLight, value) -> {
        if (value < 0f) return;
        pointLight.setRadius(value);
    };

    private static final BiConsumer<SpotLight, Float> SPOT_LIGHT_INNER_ANGLE_APPLY_HANDLER = (spotLight, value) -> {
        if (value < 0f || value >= FastMath.HALF_PI) return;
        spotLight.setSpotInnerAngle(value);
    };

    private static final BiConsumer<SpotLight, Float> SPOT_LIGHT_OUTER_ANGLE_APPLY_HANDLER = (spotLight, value) -> {
        if (value < 0f || value >= FastMath.HALF_PI) return;
        spotLight.setSpotOuterAngle(value);
    };

    private static final BiConsumer<SpotLight, Float> SPOT_LIGHT_RANGE_APPLY_HANDLER = (spotLight, value) -> {
        if (value < 0f) return;
        spotLight.setSpotRange(value);
    };

    private static final PropertyBuilder INSTANCE = new LightPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    public LightPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {
        if (object instanceof DirectionalLight) {
            buildForDirectionLight((DirectionalLight) object, container, changeConsumer);
        } else if (object instanceof SpotLight) {
            buildForSpotLight((SpotLight) object, container, changeConsumer);
        } else if (object instanceof PointLight) {
            buildForPointLight((PointLight) object, container, changeConsumer);
        }

        if (object instanceof Light) {
            buildForLight((Light) object, container, changeConsumer);
        }
    }

    private void buildForDirectionLight(@NotNull final DirectionalLight light, @NotNull final VBox container,
                                        @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f direction = light.getDirection().clone();

        final DirectionLightPropertyControl<DirectionalLight> directionControl =
                new DirectionLightPropertyControl<>(direction, Messages.MODEL_PROPERTY_DIRECTION, changeConsumer);
        directionControl.setApplyHandler(DirectionalLight::setDirection);
        directionControl.setSyncHandler(DirectionalLight::getDirection);
        directionControl.setEditObject(light);

        FXUtils.addToPane(directionControl, container);

        addSplitLine(container);
    }

    private void buildForPointLight(@NotNull final PointLight light, @NotNull final VBox container,
                                    final @NotNull ModelChangeConsumer changeConsumer) {

        final Vector3f position = light.getPosition().clone();
        final float radius = light.getRadius();

        final PositionLightPropertyControl<PointLight> positionControl =
                new PositionLightPropertyControl<>(position, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);
        positionControl.setApplyHandler(PointLight::setPosition);
        positionControl.setSyncHandler(PointLight::getPosition);
        positionControl.setEditObject(light);

        final FloatLightPropertyControl<PointLight> radiusControl =
                new FloatLightPropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);
        radiusControl.setApplyHandler(POINT_LIGHT_RADIUS_APPLY_HANDLER);
        radiusControl.setSyncHandler(PointLight::getRadius);
        radiusControl.setEditObject(light);
        radiusControl.setScrollPower(10F);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(positionControl, container);
        FXUtils.addToPane(splitLine, container);
        FXUtils.addToPane(radiusControl, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private void buildForSpotLight(@NotNull final SpotLight light, @NotNull final VBox container,
                                   @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f direction = light.getDirection().clone();
        final Vector3f position = light.getPosition().clone();

        final float innerAngle = light.getSpotInnerAngle();
        final float outerAngle = light.getSpotOuterAngle();
        final float range = light.getSpotRange();

        final DirectionLightPropertyControl<SpotLight> directionControl =
                new DirectionLightPropertyControl<>(direction, Messages.MODEL_PROPERTY_DIRECTION, changeConsumer);
        directionControl.setApplyHandler(SpotLight::setDirection);
        directionControl.setSyncHandler(SpotLight::getDirection);
        directionControl.setEditObject(light);

        final PositionLightPropertyControl<SpotLight> positionControl =
                new PositionLightPropertyControl<>(position, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);
        positionControl.setApplyHandler(SpotLight::setPosition);
        positionControl.setSyncHandler(SpotLight::getPosition);
        positionControl.setEditObject(light);

        final FloatLightPropertyControl<SpotLight> rangeControl =
                new FloatLightPropertyControl<>(range, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);
        rangeControl.setApplyHandler(SPOT_LIGHT_RANGE_APPLY_HANDLER);
        rangeControl.setSyncHandler(SpotLight::getSpotRange);
        rangeControl.setScrollPower(10F);
        rangeControl.setEditObject(light);

        final FloatLightPropertyControl<SpotLight> innerAngleControl =
                new FloatLightPropertyControl<>(innerAngle, Messages.MODEL_PROPERTY_INNER_ANGLE, changeConsumer);
        innerAngleControl.setApplyHandler(SPOT_LIGHT_INNER_ANGLE_APPLY_HANDLER);
        innerAngleControl.setSyncHandler(SpotLight::getSpotInnerAngle);
        innerAngleControl.setEditObject(light);

        final FloatLightPropertyControl<SpotLight> outerAngleControl =
                new FloatLightPropertyControl<>(outerAngle, Messages.MODEL_PROPERTY_OUTER_ANGLE, changeConsumer);
        outerAngleControl.setApplyHandler(SPOT_LIGHT_OUTER_ANGLE_APPLY_HANDLER);
        outerAngleControl.setSyncHandler(SpotLight::getSpotOuterAngle);
        outerAngleControl.setEditObject(light);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(directionControl, container);
        FXUtils.addToPane(positionControl, container);
        FXUtils.addToPane(splitLine, container);
        FXUtils.addToPane(rangeControl, container);
        FXUtils.addToPane(innerAngleControl, container);
        FXUtils.addToPane(outerAngleControl, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private void buildForLight(@NotNull final Light object, @NotNull final VBox container,
                               @NotNull final ModelChangeConsumer changeConsumer) {

        final ColorRGBA color = object.getColor();

        final ColorLightPropertyControl<Light> radiusControl =
                new ColorLightPropertyControl<>(color, Messages.MODEL_PROPERTY_COLOR, changeConsumer);
        radiusControl.setApplyHandler(Light::setColor);
        radiusControl.setSyncHandler(Light::getColor);
        radiusControl.setEditObject(object);

        FXUtils.addToPane(radiusControl, container);
    }
}
