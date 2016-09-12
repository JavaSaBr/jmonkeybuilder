package com.ss.editor.ui.control.model.property;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import tonegod.emitter.ParticleEmitterNode;

import static com.ss.editor.util.EditorUtil.clipNumber;

/**
 * The builder for building controls of settings for different nodes of a model.
 *
 * @author JavaSaBr
 */
public class PropertyBuilder {

    private static final Editor EDITOR = Editor.getInstance();

    public static final Insets SPLIT_LINE_OFFSET = new Insets(14, 0, 14, 0);

    public static final Function<BoundingVolume, String> BOUNDING_VOLUME_TO_STRING = boundingVolume -> {

        if (boundingVolume instanceof BoundingSphere) {
            final BoundingSphere boundingSphere = (BoundingSphere) boundingVolume;
            return Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE + ": " + "[" + Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_SPHERE_RADIUS + "=" + boundingSphere.getRadius() + "]";
        } else if (boundingVolume instanceof BoundingBox) {

            final BoundingBox boundingBox = (BoundingBox) boundingVolume;

            final float xExtent = clipNumber(boundingBox.getXExtent(), 100);
            final float yExtent = clipNumber(boundingBox.getYExtent(), 100);
            final float zExtent = clipNumber(boundingBox.getZExtent(), 100);

            return Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_BOX + ": " + "[x=" + xExtent + ", y=" + yExtent + ", z=" + zExtent + "]";
        }

        return StringUtils.EMPTY;
    };

    public static final BiConsumer<Geometry, MaterialKey> MATERIAL_APPLY_HANDLER = (geometry, materialKey) -> {

        final AssetManager assetManager = EDITOR.getAssetManager();

        if (materialKey == null) {

            final Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", ColorRGBA.Gray);

            geometry.setMaterial(material);

        } else {

            assetManager.clearCache();

            final Material material = assetManager.loadAsset(materialKey);
            geometry.setMaterial(material);
        }
    };

    public static final Function<Geometry, MaterialKey> MATERIAL_SYNC_HANDLER = geometry -> {
        final Material material = geometry.getMaterial();
        return (MaterialKey) material.getKey();
    };

    public static final BiConsumer<SpotLight, Float> SPOT_LIGHT_INNER_ANGLE_APPLY_HANDLER = (spotLight, value) -> {
        if (value < 0f || value >= FastMath.HALF_PI) return;
        spotLight.setSpotInnerAngle(value);
    };

    public static final BiConsumer<SpotLight, Float> SPOT_LIGHT_OUTER_ANGLE_APPLY_HANDLER = (spotLight, value) -> {
        if (value < 0f || value >= FastMath.HALF_PI) return;
        spotLight.setSpotOuterAngle(value);
    };

    public static final BiConsumer<SpotLight, Float> SPOT_LIGHT_RANGE_APPLY_HANDLER = (spotLight, value) -> {
        if (value < 0f) return;
        spotLight.setSpotRange(value);
    };

    public static final BiConsumer<PointLight, Float> POINT_LIGHT_RADIUS_APPLY_HANDLER = (pointLight, value) -> {
        if (value < 0f) return;
        pointLight.setRadius(value);
    };

    /**
     * Построесть список свойств для указанного объекта.
     *
     * @param object    объект для которого строятся свойства.
     * @param container контейнер контролов.
     */
    public static void buildFor(@NotNull final Object object, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {

        if (object instanceof DirectionalLight) {
            buildForDirectionLight((DirectionalLight) object, container, modelChangeConsumer);
        } else if (object instanceof SpotLight) {
            buildForSpotLight((SpotLight) object, container, modelChangeConsumer);
        } else if (object instanceof PointLight) {
            buildForPointLight((PointLight) object, container, modelChangeConsumer);
        }

        if (object instanceof Light) {
            buildForLight((Light) object, container, modelChangeConsumer);
        }

        if (object instanceof ParticleEmitterNode) {

            final ParticleEmitterNode emitterNode = (ParticleEmitterNode) object;

            createEmissionControls(container, modelChangeConsumer, emitterNode);

            final boolean testParticles = emitterNode.isEnabledTestParticles();

            final ParticleEmitterNode.BillboardMode billboardMode = emitterNode.getBillboardMode();

            final ModelPropertyControl<ParticleEmitterNode, Boolean> testParticlesModeControl = new BooleanModelPropertyControl<>(testParticles, "Test Particles Mode", modelChangeConsumer);
            testParticlesModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestParticles);
            testParticlesModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestParticles);
            testParticlesModeControl.setEditObject(emitterNode);

            final ModelPropertyControl<ParticleEmitterNode, ParticleEmitterNode.BillboardMode> billboardModeControl = new BillboardModelPropertyControl(billboardMode, "Billboard Mode", modelChangeConsumer);
            billboardModeControl.setApplyHandler(ParticleEmitterNode::setBillboardMode);
            billboardModeControl.setSyncHandler(ParticleEmitterNode::getBillboardMode);
            billboardModeControl.setEditObject(emitterNode);

            final Line splitLine = createSplitLine(container);

            FXUtils.addToPane(testParticlesModeControl, container);
            FXUtils.addToPane(billboardModeControl, container);
            FXUtils.addToPane(splitLine, container);

            VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
        }

        if (object instanceof Mesh) {
            //TODO
        } else if (object instanceof Geometry) {
            buildForGeometry((Geometry) object, container, modelChangeConsumer);
        } else if (object instanceof Node) {
            //TODO
        }

        if (object instanceof Spatial) {
            buildForSpatial((Spatial) object, container, modelChangeConsumer);
        }
    }

    private static void createEmissionControls(@NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final ParticleEmitterNode emitterNode) {

        final boolean enabledTestEmitter = emitterNode.isEnabledTestEmitter();
        final boolean enabled = emitterNode.isEnabled();
        final boolean randomEmissionPoint = emitterNode.isUseRandomEmissionPoint();
        final boolean sequentialEmissionFace = emitterNode.isUseSequentialEmissionFace();
        final boolean skipPattern = emitterNode.isUseSequentialSkipPattern();

        final int maxParticles = emitterNode.getMaxParticles();
        final int emissionsPerSecond = emitterNode.getEmissionsPerSecond();
        final int particlesPerEmission = emitterNode.getParticlesPerEmission();

        final ModelPropertyControl<ParticleEmitterNode, Boolean> testEmitterModeControl = new BooleanModelPropertyControl<>(enabledTestEmitter, "Test Mode", modelChangeConsumer);
        testEmitterModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestEmitter);
        testEmitterModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestEmitter);
        testEmitterModeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> enableControl = new BooleanModelPropertyControl<>(enabled, "Enabled", modelChangeConsumer);
        enableControl.setApplyHandler(ParticleEmitterNode::setEnabled);
        enableControl.setSyncHandler(ParticleEmitterNode::isEnabled);
        enableControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> randomPointControl = new BooleanModelPropertyControl<>(randomEmissionPoint, "Random Point", modelChangeConsumer);
        randomPointControl.setApplyHandler(ParticleEmitterNode::setUseRandomEmissionPoint);
        randomPointControl.setSyncHandler(ParticleEmitterNode::isUseRandomEmissionPoint);
        randomPointControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> sequentialFaceControl = new BooleanModelPropertyControl<>(sequentialEmissionFace, "Sequential Face", modelChangeConsumer);
        sequentialFaceControl.setApplyHandler(ParticleEmitterNode::setUseSequentialEmissionFace);
        sequentialFaceControl.setSyncHandler(ParticleEmitterNode::isUseSequentialEmissionFace);
        sequentialFaceControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> skipPatternControl = new BooleanModelPropertyControl<>(skipPattern, "Skip Pattern", modelChangeConsumer);
        skipPatternControl.setApplyHandler(ParticleEmitterNode::setUseSequentialSkipPattern);
        skipPatternControl.setSyncHandler(ParticleEmitterNode::isUseSequentialSkipPattern);
        skipPatternControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> maxParticlesControl = new IntegerModelPropertyControl<>(maxParticles, "Max Particles", modelChangeConsumer);
        maxParticlesControl.setApplyHandler(ParticleEmitterNode::setMaxParticles);
        maxParticlesControl.setSyncHandler(ParticleEmitterNode::getMaxParticles);
        maxParticlesControl.setScrollIncrement(30);
        maxParticlesControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> emissionPerSecControl = new IntegerModelPropertyControl<>(emissionsPerSecond, "Emission per second", modelChangeConsumer);
        emissionPerSecControl.setApplyHandler(ParticleEmitterNode::setEmissionsPerSecond);
        emissionPerSecControl.setSyncHandler(ParticleEmitterNode::getEmissionsPerSecond);
        emissionPerSecControl.setScrollIncrement(30);
        emissionPerSecControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> particlesPerEmissionControl = new IntegerModelPropertyControl<>(particlesPerEmission, "Particles per Emission", modelChangeConsumer);
        particlesPerEmissionControl.setApplyHandler(ParticleEmitterNode::setParticlesPerEmission);
        particlesPerEmissionControl.setSyncHandler(ParticleEmitterNode::getParticlesPerEmission);
        particlesPerEmissionControl.setScrollIncrement(30);
        particlesPerEmissionControl.setEditObject(emitterNode);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(testEmitterModeControl, container);
        FXUtils.addToPane(enableControl, container);
        FXUtils.addToPane(randomPointControl, container);
        FXUtils.addToPane(sequentialFaceControl, container);
        FXUtils.addToPane(skipPatternControl, container);
        FXUtils.addToPane(maxParticlesControl, container);
        FXUtils.addToPane(emissionPerSecControl, container);
        FXUtils.addToPane(particlesPerEmissionControl, container);
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private static void buildForSpatial(@NotNull final Spatial spatial, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {

        final Spatial.CullHint cullHint = spatial.getLocalCullHint();
        final RenderQueue.ShadowMode shadowMode = spatial.getLocalShadowMode();
        final RenderQueue.Bucket queueBucket = spatial.getLocalQueueBucket();
        final Vector3f location = spatial.getLocalTranslation().clone();
        final Vector3f scale = spatial.getLocalScale().clone();

        final Quaternion rotation = spatial.getLocalRotation().clone();

        final ModelPropertyControl<Spatial, Spatial.CullHint> cullHintControl = new CullHintModelPropertyControl(cullHint, Messages.MODEL_PROPERTY_CULL_HINT, modelChangeConsumer);
        cullHintControl.setApplyHandler(Spatial::setCullHint);
        cullHintControl.setSyncHandler(Spatial::getLocalCullHint);
        cullHintControl.setEditObject(spatial);

        final ModelPropertyControl<Spatial, RenderQueue.ShadowMode> shadowModeControl = new ShadowModeModelPropertyControl(shadowMode, Messages.MODEL_PROPERTY_SHADOW_MODE, modelChangeConsumer);
        shadowModeControl.setApplyHandler(Spatial::setShadowMode);
        shadowModeControl.setSyncHandler(Spatial::getLocalShadowMode);
        shadowModeControl.setEditObject(spatial);

        final ModelPropertyControl<Spatial, RenderQueue.Bucket> queueBucketControl = new QueueBucketModelPropertyControl(queueBucket, Messages.MODEL_PROPERTY_QUEUE_BUCKET, modelChangeConsumer);
        queueBucketControl.setApplyHandler(Spatial::setQueueBucket);
        queueBucketControl.setSyncHandler(Spatial::getLocalQueueBucket);
        queueBucketControl.setEditObject(spatial);

        final ModelPropertyControl<Spatial, Vector3f> locationControl = new Vector3fModelPropertyControl(location, Messages.MODEL_PROPERTY_LOCATION, modelChangeConsumer);
        locationControl.setApplyHandler(Spatial::setLocalTranslation);
        locationControl.setSyncHandler(Spatial::getLocalTranslation);
        locationControl.setEditObject(spatial);

        final ModelPropertyControl<Spatial, Vector3f> scaleControl = new Vector3fModelPropertyControl(scale, Messages.MODEL_PROPERTY_SCALE, modelChangeConsumer);
        scaleControl.setApplyHandler(Spatial::setLocalScale);
        scaleControl.setSyncHandler(Spatial::getLocalScale);
        scaleControl.setEditObject(spatial);

        final ModelPropertyControl<Spatial, Quaternion> rotationControl = new QuaternionModelPropertyControl(rotation, Messages.MODEL_PROPERTY_ROTATION, modelChangeConsumer);
        rotationControl.setApplyHandler(Spatial::setLocalRotation);
        rotationControl.setSyncHandler(Spatial::getLocalRotation);
        rotationControl.setEditObject(spatial);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(cullHintControl, container);
        FXUtils.addToPane(shadowModeControl, container);
        FXUtils.addToPane(queueBucketControl, container);
        FXUtils.addToPane(splitLine, container);
        FXUtils.addToPane(locationControl, container);
        FXUtils.addToPane(scaleControl, container);
        FXUtils.addToPane(rotationControl, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private static void buildForGeometry(@NotNull final Geometry geometry, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {

        final Material material = geometry.getMaterial();
        final MaterialKey materialKey = (MaterialKey) material.getKey();
        final BoundingVolume modelBound = geometry.getModelBound();

        final ModelPropertyControl<Geometry, MaterialKey> materialControl = new MaterialModelPropertyEditor(materialKey, Messages.MODEL_PROPERTY_MATERIAL, modelChangeConsumer);
        materialControl.setApplyHandler(MATERIAL_APPLY_HANDLER);
        materialControl.setSyncHandler(MATERIAL_SYNC_HANDLER);
        materialControl.setEditObject(geometry);

        final DefaultModelPropertyControl<BoundingVolume> boundingVolumeControl = new DefaultModelPropertyControl<>(modelBound, Messages.BOUNDING_VOLUME_MODEL_PROPERTY_CONTROL_NAME, modelChangeConsumer);
        boundingVolumeControl.setToStringFunction(BOUNDING_VOLUME_TO_STRING);
        boundingVolumeControl.reload();
        boundingVolumeControl.setEditObject(geometry);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(materialControl, container);
        FXUtils.addToPane(boundingVolumeControl, container);
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private static void buildForLight(@NotNull final Light object, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {

        final ColorRGBA color = object.getColor();

        final ModelPropertyControl<Light, ColorRGBA> radiusControl = new ColorLightPropertyControl<>(color, Messages.MODEL_PROPERTY_COLOR, modelChangeConsumer);
        radiusControl.setApplyHandler(Light::setColor);
        radiusControl.setSyncHandler(Light::getColor);
        radiusControl.setEditObject(object);

        FXUtils.addToPane(radiusControl, container);
    }

    private static void buildForPointLight(@NotNull final PointLight light, @NotNull final VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer) {

        final Vector3f position = light.getPosition().clone();
        final float radius = light.getRadius();

        final ModelPropertyControl<PointLight, Vector3f> positionControl = new PositionLightPropertyControl<>(position, Messages.MODEL_PROPERTY_LOCATION, modelChangeConsumer);
        positionControl.setApplyHandler(PointLight::setPosition);
        positionControl.setSyncHandler(PointLight::getPosition);
        positionControl.setEditObject(light);

        final FloatLightPropertyControl<PointLight> radiusControl = new FloatLightPropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, modelChangeConsumer);
        radiusControl.setApplyHandler(POINT_LIGHT_RADIUS_APPLY_HANDLER);
        radiusControl.setSyncHandler(PointLight::getRadius);
        radiusControl.setEditObject(light);
        radiusControl.setScrollIncrement(10F);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(positionControl, container);
        FXUtils.addToPane(splitLine, container);
        FXUtils.addToPane(radiusControl, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private static void buildForSpotLight(@NotNull final SpotLight light, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {

        final Vector3f direction = light.getDirection().clone();
        final Vector3f position = light.getPosition().clone();
        final float innerAngle = light.getSpotInnerAngle();
        final float outerAngle = light.getSpotOuterAngle();
        final float range = light.getSpotRange();

        final ModelPropertyControl<SpotLight, Vector3f> directionControl = new DirectionLightPropertyControl<>(direction, Messages.MODEL_PROPERTY_DIRECTION, modelChangeConsumer);
        directionControl.setApplyHandler(SpotLight::setDirection);
        directionControl.setSyncHandler(SpotLight::getDirection);
        directionControl.setEditObject(light);

        final ModelPropertyControl<SpotLight, Vector3f> positionControl = new PositionLightPropertyControl<>(position, Messages.MODEL_PROPERTY_LOCATION, modelChangeConsumer);
        positionControl.setApplyHandler(SpotLight::setPosition);
        positionControl.setSyncHandler(SpotLight::getPosition);
        positionControl.setEditObject(light);

        final FloatLightPropertyControl<SpotLight> rangeControl = new FloatLightPropertyControl<>(range, Messages.MODEL_PROPERTY_RADIUS, modelChangeConsumer);
        rangeControl.setApplyHandler(SPOT_LIGHT_RANGE_APPLY_HANDLER);
        rangeControl.setSyncHandler(SpotLight::getSpotRange);
        rangeControl.setScrollIncrement(10F);
        rangeControl.setEditObject(light);

        final ModelPropertyControl<SpotLight, Float> innerAngleControl = new FloatLightPropertyControl<>(innerAngle, Messages.MODEL_PROPERTY_INNER_ANGLE, modelChangeConsumer);
        innerAngleControl.setApplyHandler(SPOT_LIGHT_INNER_ANGLE_APPLY_HANDLER);
        innerAngleControl.setSyncHandler(SpotLight::getSpotInnerAngle);
        innerAngleControl.setEditObject(light);

        final ModelPropertyControl<SpotLight, Float> outerAngleControl = new FloatLightPropertyControl<>(outerAngle, Messages.MODEL_PROPERTY_OUTER_ANGLE, modelChangeConsumer);
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

    private static void buildForDirectionLight(@NotNull final DirectionalLight light, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        final Vector3f direction = light.getDirection().clone();

        final ModelPropertyControl<DirectionalLight, Vector3f> directionControl = new DirectionLightPropertyControl<>(direction, Messages.MODEL_PROPERTY_DIRECTION, modelChangeConsumer);
        directionControl.setApplyHandler(DirectionalLight::setDirection);
        directionControl.setSyncHandler(DirectionalLight::getDirection);
        directionControl.setEditObject(light);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(directionControl, container);
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    /**
     * Создать линию-разделителя.
     */
    @NotNull
    private static Line createSplitLine(@NotNull final VBox container) {

        final Line line = new Line();
        line.setId(CSSIds.MODEL_PARAM_CONTROL_SPLIT_LINE);
        line.setStartX(0);
        line.endXProperty().bind(container.widthProperty().subtract(70));

        return line;
    }
}
