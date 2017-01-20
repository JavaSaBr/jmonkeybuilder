package com.ss.editor.ui.control.model.property.builder.impl;

import static com.ss.editor.util.NodeUtils.findParent;

import com.jme3.math.Vector2f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.EnumModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.IntegerModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.MinMaxModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.particle.MaterialEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.control.particle.ParticlesSpriteCountModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import tonegod.emitter.BillboardMode;
import tonegod.emitter.EmissionPoint;
import tonegod.emitter.EmitterMesh.DirectionType;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.material.ParticlesMaterial;
import tonegod.emitter.node.ParticleNode;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link ParticleEmitterNode}
 * objects.
 *
 * @author JavaSaBr
 */
public class ParticlesPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    public static final DirectionType[] DIRECTION_TYPES = DirectionType.values();
    public static final EmissionPoint[] PARTICLE_EMISSION_POINTS = EmissionPoint.values();

    private static final PropertyBuilder INSTANCE = new ParticlesPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    public ParticlesPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        if (object instanceof ParticleEmitterNode) {
            createEmissionControls(container, changeConsumer, (ParticleEmitterNode) object);
        } else if (object instanceof ParticleNode) {
            createParticlesControls(container, changeConsumer, (ParticleNode) object);
        }
    }

    private void createParticlesControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer changeConsumer,
                                         @NotNull final ParticleNode particleGeometry) {

        final ParticleEmitterNode emitterNode = findParent(particleGeometry, spatial -> spatial instanceof ParticleEmitterNode);

        Objects.requireNonNull(emitterNode);

        final boolean testParticles = emitterNode.isEnabledTestParticles();
        final boolean particlesFollowEmitter = emitterNode.isParticlesFollowEmitter();

        final BillboardMode billboardMode = emitterNode.getBillboardMode();

        final Vector2f forceMinMax = emitterNode.getForceMinMax();
        final Vector2f lifeMinMax = emitterNode.getLifeMinMax();
        final Vector2f spriteCount = emitterNode.getSpriteCount();

        final float stretchFactor = emitterNode.getVelocityStretchFactor();

        final ParticlesMaterial particlesMaterial = emitterNode.getParticlesMaterial();

        final ModelPropertyControl<ParticleEmitterNode, ParticlesMaterial> materialControl =
                new MaterialEmitterPropertyControl(particlesMaterial, Messages.MODEL_PROPERTY_MATERIAL, changeConsumer);
        materialControl.setApplyHandler(ParticleEmitterNode::setParticlesMaterial);
        materialControl.setSyncHandler(ParticleEmitterNode::getParticlesMaterial);
        materialControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> testParticlesModeControl =
                new BooleanModelPropertyControl<>(testParticles, Messages.PARTICLE_EMITTER_TEST_PARTICLES, changeConsumer);
        testParticlesModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestParticles);
        testParticlesModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestParticles);
        testParticlesModeControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> particlesFollowEmitControl =
                new BooleanModelPropertyControl<>(particlesFollowEmitter, Messages.PARTICLE_EMITTER_FOLLOW_EMITTER, changeConsumer);
        particlesFollowEmitControl.setApplyHandler(ParticleEmitterNode::setParticlesFollowEmitter);
        particlesFollowEmitControl.setSyncHandler(ParticleEmitterNode::isParticlesFollowEmitter);
        particlesFollowEmitControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> particlesStretchingControl =
                new BooleanModelPropertyControl<>(particlesFollowEmitter, Messages.PARTICLE_EMITTER_STRETCHING, changeConsumer);
        particlesStretchingControl.setApplyHandler(ParticleEmitterNode::setVelocityStretching);
        particlesStretchingControl.setSyncHandler(ParticleEmitterNode::isVelocityStretching);
        particlesStretchingControl.setEditObject(emitterNode);

        final FloatModelPropertyControl<ParticleEmitterNode> magnitudeControl =
                new FloatModelPropertyControl<>(stretchFactor, Messages.PARTICLE_EMITTER_MAGNITUDE, changeConsumer);
        magnitudeControl.setApplyHandler(ParticleEmitterNode::setVelocityStretchFactor);
        magnitudeControl.setSyncHandler(ParticleEmitterNode::getVelocityStretchFactor);
        magnitudeControl.setEditObject(emitterNode);

        final EnumModelPropertyControl<ParticleEmitterNode, BillboardMode> billboardModeControl =
                new EnumModelPropertyControl<>(billboardMode, Messages.PARTICLE_EMITTER_BILLBOARD, changeConsumer, BillboardMode.values());
        billboardModeControl.setApplyHandler(ParticleEmitterNode::setBillboardMode);
        billboardModeControl.setSyncHandler(ParticleEmitterNode::getBillboardMode);
        billboardModeControl.setEditObject(emitterNode);

        final MinMaxModelPropertyControl<ParticleEmitterNode> forceMinMaxControl =
                new MinMaxModelPropertyControl<>(forceMinMax, Messages.PARTICLE_EMITTER_INITIAL_FORCE, changeConsumer);
        forceMinMaxControl.setApplyHandler(ParticleEmitterNode::setForceMinMax);
        forceMinMaxControl.setSyncHandler(ParticleEmitterNode::getForceMinMax);
        forceMinMaxControl.setEditObject(emitterNode);

        final MinMaxModelPropertyControl<ParticleEmitterNode> lifeMinMaxControl =
                new MinMaxModelPropertyControl<>(lifeMinMax, Messages.PARTICLE_EMITTER_LIFE, changeConsumer);
        lifeMinMaxControl.setApplyHandler(ParticleEmitterNode::setLifeMinMax);
        lifeMinMaxControl.setSyncHandler(ParticleEmitterNode::getLifeMinMax);
        lifeMinMaxControl.setEditObject(emitterNode);

        final ParticlesSpriteCountModelPropertyControl spriteCountControl =
                new ParticlesSpriteCountModelPropertyControl(spriteCount, Messages.PARTICLE_EMITTER_SPRITE_COUNT, changeConsumer);
        spriteCountControl.setApplyHandler(ParticleEmitterNode::setSpriteCount);
        spriteCountControl.setSyncHandler(ParticleEmitterNode::getSpriteCount);
        spriteCountControl.setEditObject(emitterNode);

        FXUtils.addToPane(testParticlesModeControl, container);
        FXUtils.addToPane(particlesFollowEmitControl, container);
        FXUtils.addToPane(particlesStretchingControl, container);
        FXUtils.addToPane(magnitudeControl, container);
        FXUtils.addToPane(billboardModeControl, container);
        FXUtils.addToPane(materialControl, container);
        FXUtils.addToPane(spriteCountControl, container);
        FXUtils.addToPane(forceMinMaxControl, container);
        FXUtils.addToPane(lifeMinMaxControl, container);

        addSplitLine(container);
    }

    private void createEmissionControls(@NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer,
                                        @NotNull final ParticleEmitterNode emitterNode) {

        final boolean enabledTestEmitter = emitterNode.isEnabledTestEmitter();
        final boolean enabled = emitterNode.isEnabled();
        final boolean randomEmissionPoint = emitterNode.isRandomEmissionPoint();
        final boolean sequentialEmissionFace = emitterNode.isSequentialEmissionFace();
        final boolean skipPattern = emitterNode.isSequentialSkipPattern();

        final int maxParticles = emitterNode.getMaxParticles();
        final int emissionsPerSecond = emitterNode.getEmissionsPerSecond();
        final int particlesPerEmission = emitterNode.getParticlesPerEmission();
        final float emitterLife = emitterNode.getEmitterLife();

        final DirectionType directionType = emitterNode.getDirectionType();
        final EmissionPoint emissionPoint = emitterNode.getEmissionPoint();

        final BooleanModelPropertyControl<ParticleEmitterNode> testEmitterModeControl =
                new BooleanModelPropertyControl<>(enabledTestEmitter, Messages.PARTICLE_EMITTER_TEST_MODE, modelChangeConsumer);
        testEmitterModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestEmitter);
        testEmitterModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestEmitter);
        testEmitterModeControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> enableControl =
                new BooleanModelPropertyControl<>(enabled, Messages.PARTICLE_EMITTER_ENABLED, modelChangeConsumer);
        enableControl.setApplyHandler(ParticleEmitterNode::setEnabled);
        enableControl.setSyncHandler(ParticleEmitterNode::isEnabled);
        enableControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> randomPointControl =
                new BooleanModelPropertyControl<>(randomEmissionPoint, Messages.PARTICLE_EMITTER_RANDOM_POINT, modelChangeConsumer);
        randomPointControl.setApplyHandler(ParticleEmitterNode::setRandomEmissionPoint);
        randomPointControl.setSyncHandler(ParticleEmitterNode::isRandomEmissionPoint);
        randomPointControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> sequentialFaceControl =
                new BooleanModelPropertyControl<>(sequentialEmissionFace, Messages.PARTICLE_EMITTER_SEQUENTIAL_FACE, modelChangeConsumer);
        sequentialFaceControl.setApplyHandler(ParticleEmitterNode::setSequentialEmissionFace);
        sequentialFaceControl.setSyncHandler(ParticleEmitterNode::isSequentialEmissionFace);
        sequentialFaceControl.setEditObject(emitterNode);

        final BooleanModelPropertyControl<ParticleEmitterNode> skipPatternControl =
                new BooleanModelPropertyControl<>(skipPattern, Messages.PARTICLE_EMITTER_SKIP_PATTERN, modelChangeConsumer);
        skipPatternControl.setApplyHandler(ParticleEmitterNode::setSequentialSkipPattern);
        skipPatternControl.setSyncHandler(ParticleEmitterNode::isSequentialSkipPattern);
        skipPatternControl.setEditObject(emitterNode);

        final EnumModelPropertyControl<ParticleEmitterNode, DirectionType> directionTypeControl =
                new EnumModelPropertyControl<>(directionType, Messages.PARTICLE_EMITTER_DIRECTION_TYPE, modelChangeConsumer, DIRECTION_TYPES);
        directionTypeControl.setApplyHandler(ParticleEmitterNode::setDirectionType);
        directionTypeControl.setSyncHandler(ParticleEmitterNode::getDirectionType);
        directionTypeControl.setEditObject(emitterNode);

        final EnumModelPropertyControl<ParticleEmitterNode, EmissionPoint> emissionPointControl =
                new EnumModelPropertyControl<>(emissionPoint, Messages.PARTICLE_EMITTER_EMISSION_POINT, modelChangeConsumer, PARTICLE_EMISSION_POINTS);
        emissionPointControl.setApplyHandler(ParticleEmitterNode::setEmissionPoint);
        emissionPointControl.setSyncHandler(ParticleEmitterNode::getEmissionPoint);
        emissionPointControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> maxParticlesControl =
                new IntegerModelPropertyControl<>(maxParticles, Messages.PARTICLE_EMITTER_MAX_PARTICLES, modelChangeConsumer);
        maxParticlesControl.setApplyHandler(ParticleEmitterNode::setMaxParticles);
        maxParticlesControl.setSyncHandler(ParticleEmitterNode::getMaxParticles);
        maxParticlesControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> emissionPerSecControl =
                new IntegerModelPropertyControl<>(emissionsPerSecond, Messages.PARTICLE_EMITTER_EMISSION_PER_SECOND, modelChangeConsumer);
        emissionPerSecControl.setApplyHandler(ParticleEmitterNode::setEmissionsPerSecond);
        emissionPerSecControl.setSyncHandler(ParticleEmitterNode::getEmissionsPerSecond);
        emissionPerSecControl.setEditObject(emitterNode);

        final IntegerModelPropertyControl<ParticleEmitterNode> particlesPerEmissionControl =
                new IntegerModelPropertyControl<>(particlesPerEmission, Messages.PARTICLE_EMITTER_PARTICLES_PER_SECOND, modelChangeConsumer);
        particlesPerEmissionControl.setApplyHandler(ParticleEmitterNode::setParticlesPerEmission);
        particlesPerEmissionControl.setSyncHandler(ParticleEmitterNode::getParticlesPerEmission);
        particlesPerEmissionControl.setEditObject(emitterNode);

        final FloatModelPropertyControl<ParticleEmitterNode> emitterLifeControl =
                new FloatModelPropertyControl<>(emitterLife, Messages.PARTICLE_EMITTER_EMITTER_LIFE, modelChangeConsumer);
        emitterLifeControl.setApplyHandler(ParticleEmitterNode::setEmitterLife);
        emitterLifeControl.setSyncHandler(ParticleEmitterNode::getEmitterLife);
        emitterLifeControl.setEditObject(emitterNode);

        FXUtils.addToPane(testEmitterModeControl, container);
        FXUtils.addToPane(enableControl, container);
        FXUtils.addToPane(randomPointControl, container);
        FXUtils.addToPane(sequentialFaceControl, container);
        FXUtils.addToPane(skipPatternControl, container);
        FXUtils.addToPane(directionTypeControl, container);
        FXUtils.addToPane(emissionPointControl, container);
        FXUtils.addToPane(maxParticlesControl, container);
        FXUtils.addToPane(emissionPerSecControl, container);
        FXUtils.addToPane(particlesPerEmissionControl, container);
        FXUtils.addToPane(emitterLifeControl, container);

        addSplitLine(container);
    }
}
