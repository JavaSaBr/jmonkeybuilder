package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.math.Vector2f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.*;
import com.ss.editor.ui.control.model.property.control.particle.MaterialEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.control.particle.ParticlesSpriteCountModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
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

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link ParticleEmitterNode}
 * objects.
 *
 * @author JavaSaBr
 */
public class ParticlesPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final DirectionType[] DIRECTION_TYPES = DirectionType.values();

    @NotNull
    private static final EmissionPoint[] PARTICLE_EMISSION_POINTS = EmissionPoint.values();

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> FOLLOW_EMITTER_HANDLER = (node, result) -> {
        node.setParticlesFollowEmitter(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> VELOCITY_STRETCHING_HANDLER = (node, result) -> {
        node.setVelocityStretching(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> RANDOM_EMISSION_POINT_HANDLER = (node, result) -> {
        node.setRandomEmissionPoint(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> SEQUENTIAL_EMISSION_FACE_HANDLER = (node, result) -> {
        node.setSequentialEmissionFace(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, DirectionType> DIRECTION_TYPE_HANDLER = (node, result) -> {
        node.setDirectionType(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Boolean> SEQUENTIAL_SKIP_PATTERN_HANDLER = (node, result) -> {
        node.setSequentialSkipPattern(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, EmissionPoint> EMISSION_POINT_HANDLER = (node, result) -> {
        node.setEmissionPoint(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Float> EMISSIONS_PER_SECOND_HANDLER = (node, result) -> {
        node.setEmissionsPerSecond(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Integer> PARTICLES_PER_EMISSION_HANDLER = (node, result) -> {
        node.setParticlesPerEmission(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Vector2f> FORCE_MIN_MAX_HANDLER = (node, result) -> {
        node.setForceMinMax(result);
        node.killAllParticles();
    };

    @NotNull
    private static final BiConsumer<ParticleEmitterNode, Vector2f> LIFE_MIN_MAX_HANDLER = (node, result) -> {
        node.setLifeMinMax(result);
        node.killAllParticles();
    };

    @NotNull
    private static final PropertyBuilder INSTANCE = new ParticlesPropertyBuilder();

    @NotNull
    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private ParticlesPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        if(!(object instanceof ParticleEmitterNode)) return;

        final ParticleEmitterNode emitterNode = (ParticleEmitterNode) object;
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

        final BooleanModelPropertyControl<ParticleEmitterNode> testEmitterModeControl =
                new BooleanModelPropertyControl<>(testEmitter, Messages.PARTICLE_EMITTER_TEST_MODE, changeConsumer);
        testEmitterModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestEmitter);
        testEmitterModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestEmitter);
        testEmitterModeControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> testParticlesModeControl =
                new BooleanModelPropertyControl<>(testParticles, Messages.PARTICLE_EMITTER_TEST_PARTICLES, changeConsumer);
        testParticlesModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestParticles);
        testParticlesModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestParticles);
        testParticlesModeControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> enableControl =
                new BooleanModelPropertyControl<>(enabled, Messages.PARTICLE_EMITTER_ENABLED, changeConsumer);
        enableControl.setApplyHandler(ParticleEmitterNode::setEnabled);
        enableControl.setSyncHandler(ParticleEmitterNode::isEnabled);
        enableControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> particlesFollowEmitControl =
                new BooleanModelPropertyControl<>(particlesFollowEmitter, Messages.PARTICLE_EMITTER_FOLLOW_EMITTER, changeConsumer);
        particlesFollowEmitControl.setApplyHandler(FOLLOW_EMITTER_HANDLER);
        particlesFollowEmitControl.setSyncHandler(ParticleEmitterNode::isParticlesFollowEmitter);
        particlesFollowEmitControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> particlesStretchingControl =
                new BooleanModelPropertyControl<>(velocityStretching, Messages.PARTICLE_EMITTER_STRETCHING, changeConsumer);
        particlesStretchingControl.setApplyHandler(VELOCITY_STRETCHING_HANDLER);
        particlesStretchingControl.setSyncHandler(ParticleEmitterNode::isVelocityStretching);
        particlesStretchingControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> randomPointControl =
                new BooleanModelPropertyControl<>(randomEmissionPoint, Messages.PARTICLE_EMITTER_RANDOM_POINT, changeConsumer);
        randomPointControl.setApplyHandler(RANDOM_EMISSION_POINT_HANDLER);
        randomPointControl.setSyncHandler(ParticleEmitterNode::isRandomEmissionPoint);
        randomPointControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> sequentialFaceControl =
                new BooleanModelPropertyControl<>(sequentialEmissionFace, Messages.PARTICLE_EMITTER_SEQUENTIAL_FACE, changeConsumer);
        sequentialFaceControl.setApplyHandler(SEQUENTIAL_EMISSION_FACE_HANDLER);
        sequentialFaceControl.setSyncHandler(ParticleEmitterNode::isSequentialEmissionFace);
        sequentialFaceControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> skipPatternControl =
                new BooleanModelPropertyControl<>(skipPattern, Messages.PARTICLE_EMITTER_SKIP_PATTERN, changeConsumer);
        skipPatternControl.setApplyHandler(SEQUENTIAL_SKIP_PATTERN_HANDLER);
        skipPatternControl.setSyncHandler(ParticleEmitterNode::isSequentialSkipPattern);
        skipPatternControl.setEditObject(emitterNode);

        final EnumModelPropertyControl<ParticleEmitterNode, DirectionType> directionTypeControl =
                new EnumModelPropertyControl<>(directionType, Messages.PARTICLE_EMITTER_DIRECTION_TYPE, changeConsumer, DIRECTION_TYPES);
        directionTypeControl.setApplyHandler(DIRECTION_TYPE_HANDLER);
        directionTypeControl.setSyncHandler(ParticleEmitterNode::getDirectionType);
        directionTypeControl.setEditObject(emitterNode);

        final EnumModelPropertyControl<ParticleEmitterNode, EmissionPoint> emissionPointControl =
                new EnumModelPropertyControl<>(emissionPoint, Messages.PARTICLE_EMITTER_EMISSION_POINT, changeConsumer, PARTICLE_EMISSION_POINTS);
        emissionPointControl.setApplyHandler(EMISSION_POINT_HANDLER);
        emissionPointControl.setSyncHandler(ParticleEmitterNode::getEmissionPoint);
        emissionPointControl.setEditObject(emitterNode);

        final EnumModelPropertyControl<ParticleEmitterNode, BillboardMode> billboardModeControl =
                new EnumModelPropertyControl<>(billboardMode, Messages.PARTICLE_EMITTER_BILLBOARD, changeConsumer, BillboardMode.values());
        billboardModeControl.setApplyHandler(ParticleEmitterNode::setBillboardMode);
        billboardModeControl.setSyncHandler(ParticleEmitterNode::getBillboardMode);
        billboardModeControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> maxParticlesControl =
                new IntegerModelPropertyControl<>(maxParticles, Messages.PARTICLE_EMITTER_MAX_PARTICLES, changeConsumer);
        maxParticlesControl.setApplyHandler(ParticleEmitterNode::setMaxParticles);
        maxParticlesControl.setSyncHandler(ParticleEmitterNode::getMaxParticles);
        maxParticlesControl.setEditObject(emitterNode);

        final FloatModelPropertyControl<ParticleEmitterNode> emissionPerSecControl =
                new FloatModelPropertyControl<>(emissionsPerSecond, Messages.PARTICLE_EMITTER_EMISSION_PER_SECOND, changeConsumer);
        emissionPerSecControl.setApplyHandler(EMISSIONS_PER_SECOND_HANDLER);
        emissionPerSecControl.setSyncHandler(ParticleEmitterNode::getEmissionsPerSecond);
        emissionPerSecControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> particlesPerEmissionControl =
                new IntegerModelPropertyControl<>(particlesPerEmission, Messages.PARTICLE_EMITTER_PARTICLES_PER_SECOND, changeConsumer);
        particlesPerEmissionControl.setApplyHandler(PARTICLES_PER_EMISSION_HANDLER);
        particlesPerEmissionControl.setSyncHandler(ParticleEmitterNode::getParticlesPerEmission);
        particlesPerEmissionControl.setEditObject(emitterNode);

        final FloatModelPropertyControl<ParticleEmitterNode> emitterLifeControl =
                new FloatModelPropertyControl<>(emitterLife, Messages.PARTICLE_EMITTER_EMITTER_LIFE, changeConsumer);
        emitterLifeControl.setApplyHandler(ParticleEmitterNode::setEmitterLife);
        emitterLifeControl.setSyncHandler(ParticleEmitterNode::getEmitterLife);
        emitterLifeControl.setEditObject(emitterNode);

        final FloatModelPropertyControl<ParticleEmitterNode> emitterDelayControl =
                new FloatModelPropertyControl<>(emitterDelay, Messages.PARTICLE_EMITTER_EMITTER_DELAY, changeConsumer);
        emitterDelayControl.setApplyHandler(ParticleEmitterNode::setEmitterDelay);
        emitterDelayControl.setSyncHandler(ParticleEmitterNode::getEmitterDelay);
        emitterDelayControl.setEditObject(emitterNode);

        final FloatModelPropertyControl<ParticleEmitterNode> magnitudeControl =
                new FloatModelPropertyControl<>(stretchFactor, Messages.PARTICLE_EMITTER_MAGNITUDE, changeConsumer);
        magnitudeControl.setApplyHandler(ParticleEmitterNode::setVelocityStretchFactor);
        magnitudeControl.setSyncHandler(ParticleEmitterNode::getVelocityStretchFactor);
        magnitudeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, ParticlesMaterial> materialControl =
                new MaterialEmitterPropertyControl(particlesMaterial, Messages.MODEL_PROPERTY_MATERIAL, changeConsumer);
        materialControl.setApplyHandler(ParticleEmitterNode::setParticlesMaterial);
        materialControl.setSyncHandler(ParticleEmitterNode::getParticlesMaterial);
        materialControl.setEditObject(emitterNode);


        final MinMaxModelPropertyControl<ParticleEmitterNode> forceMinMaxControl =
                new MinMaxModelPropertyControl<>(forceMinMax, Messages.PARTICLE_EMITTER_INITIAL_FORCE, changeConsumer);
        forceMinMaxControl.setApplyHandler(FORCE_MIN_MAX_HANDLER);
        forceMinMaxControl.setSyncHandler(ParticleEmitterNode::getForceMinMax);
        forceMinMaxControl.setEditObject(emitterNode);

        final MinMaxModelPropertyControl<ParticleEmitterNode> lifeMinMaxControl =
                new MinMaxModelPropertyControl<>(lifeMinMax, Messages.PARTICLE_EMITTER_LIFE, changeConsumer);
        lifeMinMaxControl.setApplyHandler(LIFE_MIN_MAX_HANDLER);
        lifeMinMaxControl.setSyncHandler(ParticleEmitterNode::getLifeMinMax);
        lifeMinMaxControl.setEditObject(emitterNode);

        final ParticlesSpriteCountModelPropertyControl spriteCountControl =
                new ParticlesSpriteCountModelPropertyControl(spriteCount, Messages.PARTICLE_EMITTER_SPRITE_COUNT, changeConsumer);
        spriteCountControl.setApplyHandler(ParticleEmitterNode::setSpriteCount);
        spriteCountControl.setSyncHandler(ParticleEmitterNode::getSpriteCount);
        spriteCountControl.setEditObject(emitterNode);

        FXUtils.addToPane(enableControl, container);
        FXUtils.addToPane(testEmitterModeControl, container);
        FXUtils.addToPane(testParticlesModeControl, container);
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
        FXUtils.addToPane(materialControl, container);
        FXUtils.addToPane(spriteCountControl, container);
        FXUtils.addToPane(forceMinMaxControl, container);
        FXUtils.addToPane(lifeMinMaxControl, container);

        addSplitLine(container);
    }
}
