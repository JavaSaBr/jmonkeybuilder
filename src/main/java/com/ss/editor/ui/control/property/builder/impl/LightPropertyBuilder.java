package com.ss.editor.ui.control.property.builder.impl;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.DirectionLightPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.impl.ColorPropertyControl;
import com.ss.editor.ui.control.property.impl.FloatPropertyControl;
import com.ss.editor.ui.control.property.impl.Vector3fPropertyControl;
import com.ss.rlib.fx.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link Light} objects.
 *
 * @author JavaSaBr
 */
public class LightPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new LightPropertyBuilder();

    /**
     * Get the single instance.
     *
     * @return the single instance.
     */
    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private LightPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
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

    @FxThread
    private void buildForDirectionLight(@NotNull final DirectionalLight light, @NotNull final VBox container,
                                        @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f direction = light.getDirection().clone();

        final DirectionLightPropertyControl<DirectionalLight> directionControl =
                new DirectionLightPropertyControl<>(direction, Messages.MODEL_PROPERTY_DIRECTION, changeConsumer);
        directionControl.setApplyHandler(DirectionalLight::setDirection);
        directionControl.setSyncHandler(DirectionalLight::getDirection);
        directionControl.setEditObject(light);

        FXUtils.addToPane(directionControl, container);

        buildSplitLine(container);
    }

    @FxThread
    private void buildForPointLight(@NotNull final PointLight light, @NotNull final VBox container,
                                    @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f position = light.getPosition().clone();
        final float radius = light.getRadius();

        final Vector3fPropertyControl<ModelChangeConsumer, PointLight> positionControl =
                new Vector3fPropertyControl<>(position, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);
        positionControl.setApplyHandler(PointLight::setPosition);
        positionControl.setSyncHandler(PointLight::getPosition);
        positionControl.setEditObject(light);

        final FloatPropertyControl<ModelChangeConsumer, PointLight> radiusControl =
                new FloatPropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);
        radiusControl.setApplyHandler(PointLight::setRadius);
        radiusControl.setSyncHandler(PointLight::getRadius);
        radiusControl.setMinMax(0, Integer.MAX_VALUE);
        radiusControl.setEditObject(light);

        FXUtils.addToPane(positionControl, container);
        buildSplitLine(container);
        FXUtils.addToPane(radiusControl, container);
    }

    @FxThread
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

        final Vector3fPropertyControl<ModelChangeConsumer, SpotLight> positionControl =
                new Vector3fPropertyControl<>(position, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);
        positionControl.setApplyHandler(SpotLight::setPosition);
        positionControl.setSyncHandler(SpotLight::getPosition);
        positionControl.setEditObject(light);

        final FloatPropertyControl<ModelChangeConsumer, SpotLight> rangeControl =
                new FloatPropertyControl<>(range, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);
        rangeControl.setApplyHandler(SpotLight::setSpotRange);
        rangeControl.setSyncHandler(SpotLight::getSpotRange);
        rangeControl.setMinMax(0, Integer.MAX_VALUE);
        rangeControl.setEditObject(light);

        final FloatPropertyControl<ModelChangeConsumer, SpotLight> innerAngleControl =
                new FloatPropertyControl<>(innerAngle, Messages.MODEL_PROPERTY_INNER_ANGLE, changeConsumer);
        innerAngleControl.setApplyHandler(SpotLight::setSpotInnerAngle);
        innerAngleControl.setSyncHandler(SpotLight::getSpotInnerAngle);
        innerAngleControl.setMinMax(0F, FastMath.HALF_PI);
        innerAngleControl.setScrollPower(1F);
        innerAngleControl.setEditObject(light);

        final FloatPropertyControl<ModelChangeConsumer, SpotLight> outerAngleControl =
                new FloatPropertyControl<>(outerAngle, Messages.MODEL_PROPERTY_OUTER_ANGLE, changeConsumer);
        outerAngleControl.setApplyHandler(SpotLight::setSpotOuterAngle);
        outerAngleControl.setSyncHandler(SpotLight::getSpotOuterAngle);
        outerAngleControl.setMinMax(0F, FastMath.HALF_PI);
        outerAngleControl.setScrollPower(1F);
        outerAngleControl.setEditObject(light);

        FXUtils.addToPane(directionControl, container);
        FXUtils.addToPane(positionControl, container);
        buildSplitLine(container);
        FXUtils.addToPane(rangeControl, container);
        FXUtils.addToPane(innerAngleControl, container);
        FXUtils.addToPane(outerAngleControl, container);
    }

    @FxThread
    private void buildForLight(@NotNull final Light object, @NotNull final VBox container,
                               @NotNull final ModelChangeConsumer changeConsumer) {

        final ColorRGBA color = object.getColor();

        final ColorPropertyControl<ModelChangeConsumer, Light> radiusControl =
                new ColorPropertyControl<>(color, Messages.MODEL_PROPERTY_COLOR, changeConsumer);
        radiusControl.setApplyHandler(Light::setColor);
        radiusControl.setSyncHandler(Light::getColor);
        radiusControl.setEditObject(object);

        FXUtils.addToPane(radiusControl, container);
    }
}
