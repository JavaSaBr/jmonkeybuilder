package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.particle.MaterialEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.control.particle.ParticleEmitterImagesModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.particle.Toneg0dParticleEmitterSpriteCountModelPropertyControl;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.editor.ui.control.property.impl.*;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.BillboardMode;
import tonegod.emitter.EmissionPoint;
import tonegod.emitter.EmitterMesh.DirectionType;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.material.ParticlesMaterial;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link ParticleEmitterNode} and
 * {@link ParticleEmitter} objects.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final DirectionType[] DIRECTION_TYPES = DirectionType.values();

    @NotNull
    private static final EmissionPoint[] PARTICLE_EMISSION_POINTS = EmissionPoint.values();

    @NotNull
    private static final ParticleMesh.Type[] MESH_TYPES = ParticleMesh.Type.values();

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> FOLLOW_EMITTER_HANDLER = (emitter, result) -> {
        emitter.setParticlesFollowEmitter(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> VELOCITY_STRETCHING_HANDLER = (emitter, result) -> {
        emitter.setVelocityStretching(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> RANDOM_EMISSION_POINT_HANDLER = (emitter, result) -> {
        emitter.setRandomEmissionPoint(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> SEQUENTIAL_EMISSION_FACE_HANDLER = (emitter, result) -> {
        emitter.setSequentialEmissionFace(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, DirectionType> DIRECTION_TYPE_HANDLER = (emitter, result) -> {
        emitter.setDirectionType(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> SEQUENTIAL_SKIP_PATTERN_HANDLER = (emitter, result) -> {
        emitter.setSequentialSkipPattern(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, EmissionPoint> EMISSION_POINT_HANDLER = (emitter, result) -> {
        emitter.setEmissionPoint(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Float> EMISSIONS_PER_SECOND_HANDLER = (emitter, result) -> {
        emitter.setEmissionsPerSecond(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Integer> PARTICLES_PER_EMISSION_HANDLER = (emitter, result) -> {
        emitter.setParticlesPerEmission(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Vector2f> FORCE_MIN_MAX_HANDLER = (emitter, result) -> {
        emitter.setForceMinMax(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Vector2f> LIFE_MIN_MAX_HANDLER = (emitter, result) -> {
        emitter.setLifeMinMax(result);
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitter, Vector2f> APPLY_SIZE_HANDLER = (emitter, result) -> {
        emitter.setStartSize(result.getX());
        emitter.setEndSize(result.getY());
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitter, Vector2f> APPLY_LIFE_HANDLER = (emitter, result) -> {
        emitter.setLowLife(result.getX());
        emitter.setHighLife(result.getY());
        emitter.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitter, Vector2f> APPLY_IMAGES_HANDLER = (emitter, result) -> {
        emitter.setImagesX((int) result.getX());
        emitter.setImagesY((int) result.getY());
        emitter.killAllParticles();
    };

    @NotNull
    private static final Function<ParticleEmitter, Vector2f> SYNC_SIZE_HANDLER = emitter ->
            new Vector2f(emitter.getStartSize(), emitter.getEndSize());

    @NotNull
    private static final Function<ParticleEmitter, Vector2f> SYNC_LIFE_HANDLER = emitter ->
            new Vector2f(emitter.getLowLife(), emitter.getHighLife());

    @NotNull
    private static final Function<ParticleEmitter, Vector2f> SYNC_IMAGES_HANDLER = emitter ->
            new Vector2f(emitter.getImagesX(), emitter.getImagesY());

    @NotNull
    private static final Function<ParticleEmitter, Vector3f> SYNC_FACE_NORMAL_HANDLER = emitter -> {
        final Vector3f faceNormal = emitter.getFaceNormal();
        return faceNormal == null ? new Vector3f() : faceNormal;
    };

    @NotNull
    private static final PropertyBuilder INSTANCE = new ParticleEmitterPropertyBuilder();

    /**
     * Get the single instance.
     *
     * @return the single instance
     */
    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private ParticleEmitterPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof ParticleEmitterNode || object instanceof ParticleEmitter)) {
            return;
        }

        if (object instanceof ParticleEmitterNode) {
            buildFor(container, changeConsumer, (ParticleEmitterNode) object);
        } else {
            buildFor(container, changeConsumer, (ParticleEmitter) object);
        }

        buildSplitLine(container);
    }

    @FxThread
    private void buildFor(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                          @NotNull final ParticleEmitterNode emitterNode) {

        final ParticlesMaterial particlesMaterial = emitterNode.getParticlesMaterial();

        final boolean testEmitter = emitterNode.isEnabledTestEmitter();
        final boolean testParticles = emitterNode.isEnabledTestParticles();
        final boolean enabled = emitterNode.isEnabled();
        final boolean randomEmissionPoint = emitterNode.isRandomEmissionPoint();
        final boolean sequentialEmissionFace = emitterNode.isSequentialEmissionFace();
        final boolean skipPattern = emitterNode.isSequentialSkipPattern();
        final boolean particlesFollowEmitter = emitterNode.isParticlesFollowEmitter();
        final boolean velocityStretching = emitterNode.isVelocityStretching();

        final int maxParticles = emitterNode.getMaxParticles();
        final int particlesPerEmission = emitterNode.getParticlesPerEmission();

        final float emissionsPerSecond = emitterNode.getEmissionsPerSecond();
        final float emitterLife = emitterNode.getEmitterLife();
        final float emitterDelay = emitterNode.getEmitterDelay();
        final float stretchFactor = emitterNode.getVelocityStretchFactor();

        final DirectionType directionType = emitterNode.getDirectionType();
        final BillboardMode billboardMode = emitterNode.getBillboardMode();
        final EmissionPoint emissionPoint = emitterNode.getEmissionPoint();

        final Vector2f forceMinMax = emitterNode.getForceMinMax();
        final Vector2f lifeMinMax = emitterNode.getLifeMinMax();
        final Vector2f spriteCount = emitterNode.getSpriteCount();

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> testEmitterControl =
                new BooleanPropertyControl<>(testEmitter, Messages.MODEL_PROPERTY_IS_TEST_MODE, changeConsumer);
        testEmitterControl.setApplyHandler(ParticleEmitterNode::setEnabledTestEmitter);
        testEmitterControl.setSyncHandler(ParticleEmitterNode::isEnabledTestEmitter);
        testEmitterControl.setEditObject(emitterNode);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> testParticlesControl =
                new BooleanPropertyControl<>(testParticles, Messages.MODEL_PROPERTY_IS_TEST_PARTICLES, changeConsumer);
        testParticlesControl.setApplyHandler(ParticleEmitterNode::setEnabledTestParticles);
        testParticlesControl.setSyncHandler(ParticleEmitterNode::isEnabledTestParticles);
        testParticlesControl.setEditObject(emitterNode);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> enableControl =
                new BooleanPropertyControl<>(enabled, Messages.MODEL_PROPERTY_IS_ENABLED, changeConsumer);
        enableControl.setApplyHandler(ParticleEmitterNode::setEnabled);
        enableControl.setSyncHandler(ParticleEmitterNode::isEnabled);
        enableControl.setEditObject(emitterNode);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> particlesFollowEmitControl =
                new BooleanPropertyControl<>(particlesFollowEmitter, Messages.MODEL_PROPERTY_IS_FOLLOW_EMITTER, changeConsumer);
        particlesFollowEmitControl.setApplyHandler(FOLLOW_EMITTER_HANDLER);
        particlesFollowEmitControl.setSyncHandler(ParticleEmitterNode::isParticlesFollowEmitter);
        particlesFollowEmitControl.setEditObject(emitterNode);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> particlesStretchingControl =
                new BooleanPropertyControl<>(velocityStretching, Messages.MODEL_PROPERTY_STRETCHING, changeConsumer);
        particlesStretchingControl.setApplyHandler(VELOCITY_STRETCHING_HANDLER);
        particlesStretchingControl.setSyncHandler(ParticleEmitterNode::isVelocityStretching);
        particlesStretchingControl.setEditObject(emitterNode);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> randomPointControl =
                new BooleanPropertyControl<>(randomEmissionPoint, Messages.MODEL_PROPERTY_IS_RANDOM_POINT, changeConsumer);
        randomPointControl.setApplyHandler(RANDOM_EMISSION_POINT_HANDLER);
        randomPointControl.setSyncHandler(ParticleEmitterNode::isRandomEmissionPoint);
        randomPointControl.setEditObject(emitterNode);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> sequentialFaceControl =
                new BooleanPropertyControl<>(sequentialEmissionFace, Messages.MODEL_PROPERTY_IS_SEQUENTIAL_FACE, changeConsumer);
        sequentialFaceControl.setApplyHandler(SEQUENTIAL_EMISSION_FACE_HANDLER);
        sequentialFaceControl.setSyncHandler(ParticleEmitterNode::isSequentialEmissionFace);
        sequentialFaceControl.setEditObject(emitterNode);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitterNode> skipPatternControl =
                new BooleanPropertyControl<>(skipPattern, Messages.MODEL_PROPERTY_IS_SKIP_PATTERN, changeConsumer);
        skipPatternControl.setApplyHandler(SEQUENTIAL_SKIP_PATTERN_HANDLER);
        skipPatternControl.setSyncHandler(ParticleEmitterNode::isSequentialSkipPattern);
        skipPatternControl.setEditObject(emitterNode);

        final EnumPropertyControl<ModelChangeConsumer, ParticleEmitterNode, DirectionType> directionTypeControl =
                new EnumPropertyControl<>(directionType, Messages.MODEL_PROPERTY_DIRECTION_TYPE, changeConsumer, DIRECTION_TYPES);
        directionTypeControl.setApplyHandler(DIRECTION_TYPE_HANDLER);
        directionTypeControl.setSyncHandler(ParticleEmitterNode::getDirectionType);
        directionTypeControl.setEditObject(emitterNode);

        final EnumPropertyControl<ModelChangeConsumer, ParticleEmitterNode, EmissionPoint> emissionPointControl =
                new EnumPropertyControl<>(emissionPoint, Messages.MODEL_PROPERTY_EMISSION_POINT, changeConsumer, PARTICLE_EMISSION_POINTS);
        emissionPointControl.setApplyHandler(EMISSION_POINT_HANDLER);
        emissionPointControl.setSyncHandler(ParticleEmitterNode::getEmissionPoint);
        emissionPointControl.setEditObject(emitterNode);

        final EnumPropertyControl<ModelChangeConsumer, ParticleEmitterNode, BillboardMode> billboardModeControl =
                new EnumPropertyControl<>(billboardMode, Messages.MODEL_PROPERTY_BILLBOARD, changeConsumer, BillboardMode.values());
        billboardModeControl.setApplyHandler(ParticleEmitterNode::setBillboardMode);
        billboardModeControl.setSyncHandler(ParticleEmitterNode::getBillboardMode);
        billboardModeControl.setEditObject(emitterNode);

        final IntegerPropertyControl<ModelChangeConsumer, ParticleEmitterNode> maxParticlesControl =
                new IntegerPropertyControl<>(maxParticles, Messages.MODEL_PROPERTY_MAX_PARTICLES, changeConsumer);
        maxParticlesControl.setApplyHandler(ParticleEmitterNode::setMaxParticles);
        maxParticlesControl.setSyncHandler(ParticleEmitterNode::getMaxParticles);
        maxParticlesControl.setEditObject(emitterNode);

        final FloatPropertyControl<ModelChangeConsumer, ParticleEmitterNode> emissionPerSecControl =
                new FloatPropertyControl<>(emissionsPerSecond, Messages.MODEL_PROPERTY_EMISSION_PER_SECOND, changeConsumer);
        emissionPerSecControl.setApplyHandler(EMISSIONS_PER_SECOND_HANDLER);
        emissionPerSecControl.setSyncHandler(ParticleEmitterNode::getEmissionsPerSecond);
        emissionPerSecControl.setMinMax(0.1F, Integer.MAX_VALUE);
        emissionPerSecControl.setScrollPower(3F);
        emissionPerSecControl.setEditObject(emitterNode);

        final IntegerPropertyControl<ModelChangeConsumer, ParticleEmitterNode> particlesPerEmissionControl =
                new IntegerPropertyControl<>(particlesPerEmission, Messages.MODEL_PROPERTY_PARTICLES_PER_SECOND, changeConsumer);
        particlesPerEmissionControl.setApplyHandler(PARTICLES_PER_EMISSION_HANDLER);
        particlesPerEmissionControl.setSyncHandler(ParticleEmitterNode::getParticlesPerEmission);
        particlesPerEmissionControl.setEditObject(emitterNode);

        final FloatPropertyControl<ModelChangeConsumer, ParticleEmitterNode> emitterLifeControl =
                new FloatPropertyControl<>(emitterLife, Messages.MODEL_PROPERTY_EMITTER_LIFE, changeConsumer);
        emitterLifeControl.setApplyHandler(ParticleEmitterNode::setEmitterLife);
        emitterLifeControl.setSyncHandler(ParticleEmitterNode::getEmitterLife);
        emitterLifeControl.setEditObject(emitterNode);

        final FloatPropertyControl<ModelChangeConsumer, ParticleEmitterNode> emitterDelayControl =
                new FloatPropertyControl<>(emitterDelay, Messages.MODEL_PROPERTY_EMITTER_DELAY, changeConsumer);
        emitterDelayControl.setApplyHandler(ParticleEmitterNode::setEmitterDelay);
        emitterDelayControl.setSyncHandler(ParticleEmitterNode::getEmitterDelay);
        emitterDelayControl.setEditObject(emitterNode);

        final FloatPropertyControl<ModelChangeConsumer, ParticleEmitterNode> magnitudeControl =
                new FloatPropertyControl<>(stretchFactor, Messages.MODEL_PROPERTY_MAGNITUDE, changeConsumer);
        magnitudeControl.setApplyHandler(ParticleEmitterNode::setVelocityStretchFactor);
        magnitudeControl.setSyncHandler(ParticleEmitterNode::getVelocityStretchFactor);
        magnitudeControl.setEditObject(emitterNode);

        final PropertyControl<ModelChangeConsumer, ParticleEmitterNode, ParticlesMaterial> materialControl =
                new MaterialEmitterPropertyControl(particlesMaterial, Messages.MODEL_PROPERTY_MATERIAL, changeConsumer);
        materialControl.setApplyHandler(ParticleEmitterNode::setParticlesMaterial);
        materialControl.setSyncHandler(ParticleEmitterNode::getParticlesMaterial);
        materialControl.setEditObject(emitterNode);

        final MinMaxPropertyControl<ModelChangeConsumer, ParticleEmitterNode> forceMinMaxControl =
                new MinMaxPropertyControl<>(forceMinMax, Messages.MODEL_PROPERTY_INITIAL_FORCE, changeConsumer);
        forceMinMaxControl.setApplyHandler(FORCE_MIN_MAX_HANDLER);
        forceMinMaxControl.setSyncHandler(ParticleEmitterNode::getForceMinMax);
        forceMinMaxControl.setEditObject(emitterNode);

        final MinMaxPropertyControl<ModelChangeConsumer, ParticleEmitterNode> lifeMinMaxControl =
                new MinMaxPropertyControl<>(lifeMinMax, Messages.MODEL_PROPERTY_LIFE, changeConsumer);
        lifeMinMaxControl.setApplyHandler(LIFE_MIN_MAX_HANDLER);
        lifeMinMaxControl.setSyncHandler(ParticleEmitterNode::getLifeMinMax);
        lifeMinMaxControl.setEditObject(emitterNode);

        final Toneg0dParticleEmitterSpriteCountModelPropertyControl spriteCountControl =
                new Toneg0dParticleEmitterSpriteCountModelPropertyControl(spriteCount, Messages.MODEL_PROPERTY_SPRITE_COUNT, changeConsumer);
        spriteCountControl.setApplyHandler(ParticleEmitterNode::setSpriteCount);
        spriteCountControl.setSyncHandler(ParticleEmitterNode::getSpriteCount);
        spriteCountControl.setEditObject(emitterNode);

        FXUtils.addToPane(enableControl, container);
        FXUtils.addToPane(testEmitterControl, container);
        FXUtils.addToPane(testParticlesControl, container);
        FXUtils.addToPane(randomPointControl, container);
        FXUtils.addToPane(sequentialFaceControl, container);
        FXUtils.addToPane(skipPatternControl, container);
        FXUtils.addToPane(particlesFollowEmitControl, container);
        FXUtils.addToPane(particlesStretchingControl, container);
        FXUtils.addToPane(directionTypeControl, container);
        FXUtils.addToPane(emissionPointControl, container);
        FXUtils.addToPane(billboardModeControl, container);
        FXUtils.addToPane(maxParticlesControl, container);
        FXUtils.addToPane(emissionPerSecControl, container);
        FXUtils.addToPane(particlesPerEmissionControl, container);
        FXUtils.addToPane(emitterLifeControl, container);
        FXUtils.addToPane(emitterDelayControl, container);
        FXUtils.addToPane(magnitudeControl, container);
        FXUtils.addToPane(spriteCountControl, container);
        FXUtils.addToPane(forceMinMaxControl, container);
        FXUtils.addToPane(lifeMinMaxControl, container);

        buildSplitLine(container);

        FXUtils.addToPane(materialControl, container);
    }

    @FxThread
    private void buildFor(@NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer,
                          @NotNull final ParticleEmitter emitter) {

        final boolean facingVelocity = emitter.isFacingVelocity();
        final boolean enabled = emitter.isEnabled();
        final boolean inWorldSpace = emitter.isInWorldSpace();
        final boolean randomAngle = emitter.isRandomAngle();
        final boolean selectRandomImage = emitter.isSelectRandomImage();

        final float startSize = emitter.getStartSize();
        final float endSize = emitter.getEndSize();

        final float lowLife = emitter.getLowLife();
        final float highLife = emitter.getHighLife();

        final int imagesX = emitter.getImagesX();
        final int imagesY = emitter.getImagesY();

        final int maxNumParticles = emitter.getMaxNumParticles();
        final float rotateSpeed = emitter.getRotateSpeed();

        final ColorRGBA startColor = emitter.getStartColor();
        final ColorRGBA endColor = emitter.getEndColor();

        final ParticleMesh.Type meshType = emitter.getMeshType();

        final Vector3f gravity = emitter.getGravity();
        final Vector3f faceNormal = emitter.getFaceNormal() == null? new Vector3f() : emitter.getFaceNormal();

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> enabledControl =
                new BooleanPropertyControl<>(enabled, Messages.MODEL_PROPERTY_IS_ENABLED, changeConsumer);
        enabledControl.setApplyHandler(ParticleEmitter::setEnabled);
        enabledControl.setSyncHandler(ParticleEmitter::isEnabled);
        enabledControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> facingVelocityControl =
                new BooleanPropertyControl<>(facingVelocity, Messages.MODEL_PROPERTY_IS_FACING_VELOCITY, changeConsumer);
        facingVelocityControl.setApplyHandler(ParticleEmitter::setFacingVelocity);
        facingVelocityControl.setSyncHandler(ParticleEmitter::isFacingVelocity);
        facingVelocityControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> inWorldSpaceControl =
                new BooleanPropertyControl<>(inWorldSpace, Messages.MODEL_PROPERTY_IS_IN_WORLD_SPACE, changeConsumer);
        inWorldSpaceControl.setApplyHandler(ParticleEmitter::setInWorldSpace);
        inWorldSpaceControl.setSyncHandler(ParticleEmitter::isInWorldSpace);
        inWorldSpaceControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> randomAngleControl =
                new BooleanPropertyControl<>(randomAngle, Messages.MODEL_PROPERTY_IS_RANDOM_ANGLE, changeConsumer);
        randomAngleControl.setApplyHandler(ParticleEmitter::setRandomAngle);
        randomAngleControl.setSyncHandler(ParticleEmitter::isRandomAngle);
        randomAngleControl.setEditObject(emitter);

        final BooleanPropertyControl<ModelChangeConsumer, ParticleEmitter> selectRandomImageControl =
                new BooleanPropertyControl<>(selectRandomImage, Messages.MODEL_PROPERTY_IS_SELECT_RANDOM_IMAGE, changeConsumer);
        selectRandomImageControl.setApplyHandler(ParticleEmitter::setSelectRandomImage);
        selectRandomImageControl.setSyncHandler(ParticleEmitter::isSelectRandomImage);
        selectRandomImageControl.setEditObject(emitter);

        final IntegerPropertyControl<ModelChangeConsumer, ParticleEmitter> maxNumParticlesControl =
                new IntegerPropertyControl<>(maxNumParticles, Messages.MODEL_PROPERTY_MAX_PARTICLES, changeConsumer);
        maxNumParticlesControl.setApplyHandler(ParticleEmitter::setNumParticles);
        maxNumParticlesControl.setSyncHandler(ParticleEmitter::getMaxNumParticles);
        maxNumParticlesControl.setEditObject(emitter);

        final FloatPropertyControl<ModelChangeConsumer, ParticleEmitter> rotateSpeedControl =
                new FloatPropertyControl<>(rotateSpeed, Messages.MODEL_PROPERTY_ROTATE_SPEED, changeConsumer);
        rotateSpeedControl.setApplyHandler(ParticleEmitter::setRotateSpeed);
        rotateSpeedControl.setSyncHandler(ParticleEmitter::getRotateSpeed);
        rotateSpeedControl.setEditObject(emitter);

        final EnumPropertyControl<ModelChangeConsumer, ParticleEmitter, ParticleMesh.Type> meshTypeControl =
                new EnumPropertyControl<>(meshType, Messages.MODEL_PROPERTY_MESH_TYPE, changeConsumer, MESH_TYPES);
        meshTypeControl.setApplyHandler(ParticleEmitter::setMeshType);
        meshTypeControl.setSyncHandler(ParticleEmitter::getMeshType);
        meshTypeControl.setEditObject(emitter);

        final ColorPropertyControl<ModelChangeConsumer, ParticleEmitter> startColorControl =
                new ColorPropertyControl<>(startColor, Messages.MODEL_PROPERTY_START_COLOR, changeConsumer);
        startColorControl.setApplyHandler(ParticleEmitter::setStartColor);
        startColorControl.setSyncHandler(ParticleEmitter::getStartColor);
        startColorControl.setEditObject(emitter);

        final ColorPropertyControl<ModelChangeConsumer, ParticleEmitter> endColorControl =
                new ColorPropertyControl<>(endColor, Messages.MODEL_PROPERTY_END_COLOR, changeConsumer);
        endColorControl.setApplyHandler(ParticleEmitter::setEndColor);
        endColorControl.setSyncHandler(ParticleEmitter::getEndColor);
        endColorControl.setEditObject(emitter);

        final MinMaxPropertyControl<ModelChangeConsumer, ParticleEmitter> sizeControl =
                new MinMaxPropertyControl<>(new Vector2f(startSize, endSize), Messages.MODEL_PROPERTY_SIZE, changeConsumer);
        sizeControl.setApplyHandler(APPLY_SIZE_HANDLER);
        sizeControl.setSyncHandler(SYNC_SIZE_HANDLER);
        sizeControl.setEditObject(emitter);

        final MinMaxPropertyControl<ModelChangeConsumer, ParticleEmitter> lifeControl =
                new MinMaxPropertyControl<>(new Vector2f(lowLife, highLife), Messages.MODEL_PROPERTY_LIFE, changeConsumer);
        lifeControl.setApplyHandler(APPLY_LIFE_HANDLER);
        lifeControl.setSyncHandler(SYNC_LIFE_HANDLER);
        lifeControl.setEditObject(emitter);

        final ParticleEmitterImagesModelPropertyControl imagesControl =
                new ParticleEmitterImagesModelPropertyControl(new Vector2f(imagesX, imagesY), Messages.MODEL_PROPERTY_SPRITE_COUNT, changeConsumer);
        imagesControl.setApplyHandler(APPLY_IMAGES_HANDLER);
        imagesControl.setSyncHandler(SYNC_IMAGES_HANDLER);
        imagesControl.setEditObject(emitter);

        final Vector3FPropertyControl<ModelChangeConsumer, ParticleEmitter> gravityControl =
                new Vector3FPropertyControl<>(gravity, Messages.MODEL_PROPERTY_GRAVITY, changeConsumer);
        gravityControl.setApplyHandler(ParticleEmitter::setGravity);
        gravityControl.setSyncHandler(ParticleEmitter::getGravity);
        gravityControl.setEditObject(emitter);

        final Vector3FPropertyControl<ModelChangeConsumer, ParticleEmitter> faceNormalControl =
                new Vector3FPropertyControl<>(faceNormal, Messages.MODEL_PROPERTY_FACE_NORMAL, changeConsumer);
        faceNormalControl.setApplyHandler(ParticleEmitter::setFaceNormal);
        faceNormalControl.setSyncHandler(SYNC_FACE_NORMAL_HANDLER);
        faceNormalControl.setEditObject(emitter);

        FXUtils.addToPane(enabledControl, container);
        FXUtils.addToPane(facingVelocityControl, container);
        FXUtils.addToPane(inWorldSpaceControl, container);
        FXUtils.addToPane(randomAngleControl, container);
        FXUtils.addToPane(selectRandomImageControl, container);
        FXUtils.addToPane(maxNumParticlesControl, container);
        FXUtils.addToPane(rotateSpeedControl, container);
        FXUtils.addToPane(meshTypeControl, container);
        FXUtils.addToPane(startColorControl, container);
        FXUtils.addToPane(endColorControl, container);
        FXUtils.addToPane(sizeControl, container);
        FXUtils.addToPane(lifeControl, container);
        FXUtils.addToPane(imagesControl, container);

        buildSplitLine(container);

        FXUtils.addToPane(gravityControl, container);
        FXUtils.addToPane(faceNormalControl, container);
    }
}
