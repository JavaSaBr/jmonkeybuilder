package com.ss.editor.ui.control.model.property.builder.impl;

import static com.ss.editor.util.NodeUtils.findParent;

import com.jme3.math.Vector2f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.EnumModelPropertyControl;
import com.ss.editor.ui.control.model.property.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.IntegerModelPropertyControl;
import com.ss.editor.ui.control.model.property.MaterialEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.MinMaxModelPropertyControl;
import com.ss.editor.ui.control.model.property.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.model.property.particle.ParticlesSpriteCountModelPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;
import tonegod.emitter.EmitterMesh.DirectionType;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.ParticleEmitterNode.BillboardMode;
import tonegod.emitter.ParticleEmitterNode.ParticleEmissionPoint;
import tonegod.emitter.material.ParticlesMaterial;
import tonegod.emitter.node.ParticleNode;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link
 * ParticleEmitterNode} objects.
 *
 * @author JavaSaBr
 */
public class ParticlesEmissionPropertyBuilder extends AbstractPropertyBuilder {

    private static final PropertyBuilder INSTANCE = new ParticlesEmissionPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {

        if (object instanceof ParticleEmitterNode) {
            createEmissionControls(container, modelChangeConsumer, (ParticleEmitterNode) object);
        } else if (object instanceof ParticleNode) {
            createParticlesControls(container, modelChangeConsumer, (ParticleNode) object);
        }
    }

    private void createParticlesControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final ParticleNode particleGeometry) {

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
                new MaterialEmitterPropertyControl(particlesMaterial, Messages.MODEL_PROPERTY_MATERIAL, modelChangeConsumer);
        materialControl.setApplyHandler(ParticleEmitterNode::setParticlesMaterial);
        materialControl.setSyncHandler(ParticleEmitterNode::getParticlesMaterial);
        materialControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> testParticlesModeControl =
                new BooleanModelPropertyControl<>(testParticles, Messages.PARTICLE_EMITTER_TEST_PARTICLES, modelChangeConsumer);
        testParticlesModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestParticles);
        testParticlesModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestParticles);
        testParticlesModeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> particlesFollowEmitControl =
                new BooleanModelPropertyControl<>(particlesFollowEmitter, Messages.PARTICLE_EMITTER_FOLLOW_EMITTER, modelChangeConsumer);
        particlesFollowEmitControl.setApplyHandler(ParticleEmitterNode::setParticlesFollowEmitter);
        particlesFollowEmitControl.setSyncHandler(ParticleEmitterNode::isParticlesFollowEmitter);
        particlesFollowEmitControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> particlesStretchingControl =
                new BooleanModelPropertyControl<>(particlesFollowEmitter, Messages.PARTICLE_EMITTER_STRETCHING, modelChangeConsumer);
        particlesStretchingControl.setApplyHandler(ParticleEmitterNode::setVelocityStretching);
        particlesStretchingControl.setSyncHandler(ParticleEmitterNode::isVelocityStretching);
        particlesStretchingControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Float> magnitudeControl =
                new FloatModelPropertyControl<>(stretchFactor, Messages.PARTICLE_EMITTER_MAGNITUDE, modelChangeConsumer);
        magnitudeControl.setApplyHandler(ParticleEmitterNode::setVelocityStretchFactor);
        magnitudeControl.setSyncHandler(ParticleEmitterNode::getVelocityStretchFactor);
        magnitudeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, BillboardMode> billboardModeControl =
                new EnumModelPropertyControl<>(billboardMode, Messages.PARTICLE_EMITTER_BILLBOARD, modelChangeConsumer, BillboardMode.values());
        billboardModeControl.setApplyHandler(ParticleEmitterNode::setBillboardMode);
        billboardModeControl.setSyncHandler(ParticleEmitterNode::getBillboardMode);
        billboardModeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Vector2f> forceMinMaxControl =
                new MinMaxModelPropertyControl<>(forceMinMax, Messages.PARTICLE_EMITTER_INITIAL_FORCE, modelChangeConsumer);
        forceMinMaxControl.setApplyHandler(ParticleEmitterNode::setForceMinMax);
        forceMinMaxControl.setSyncHandler(ParticleEmitterNode::getForceMinMax);
        forceMinMaxControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Vector2f> lifeMinMaxControl =
                new MinMaxModelPropertyControl<>(lifeMinMax, Messages.PARTICLE_EMITTER_LIFE, modelChangeConsumer);
        lifeMinMaxControl.setApplyHandler(ParticleEmitterNode::setLifeMinMax);
        lifeMinMaxControl.setSyncHandler(ParticleEmitterNode::getLifeMinMax);
        lifeMinMaxControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Vector2f> spriteCountControl =
                new ParticlesSpriteCountModelPropertyControl(spriteCount, Messages.PARTICLE_EMITTER_SPRITE_COUNT, modelChangeConsumer);
        spriteCountControl.setApplyHandler(ParticleEmitterNode::setSpriteCount);
        spriteCountControl.setSyncHandler(ParticleEmitterNode::getSpriteCount);
        spriteCountControl.setEditObject(emitterNode);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(testParticlesModeControl, container);
        FXUtils.addToPane(particlesFollowEmitControl, container);
        FXUtils.addToPane(particlesStretchingControl, container);
        FXUtils.addToPane(magnitudeControl, container);
        FXUtils.addToPane(billboardModeControl, container);
        FXUtils.addToPane(materialControl, container);
        FXUtils.addToPane(spriteCountControl, container);
        FXUtils.addToPane(forceMinMaxControl, container);
        FXUtils.addToPane(lifeMinMaxControl, container);
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private void createEmissionControls(@NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final ParticleEmitterNode emitterNode) {

        final boolean enabledTestEmitter = emitterNode.isEnabledTestEmitter();
        final boolean enabled = emitterNode.isEnabled();
        final boolean randomEmissionPoint = emitterNode.isRandomEmissionPoint();
        final boolean sequentialEmissionFace = emitterNode.isSequentialEmissionFace();
        final boolean skipPattern = emitterNode.isSequentialSkipPattern();

        final int maxParticles = emitterNode.getMaxParticles();
        final int emissionsPerSecond = emitterNode.getEmissionsPerSecond();
        final int particlesPerEmission = emitterNode.getParticlesPerEmission();

        final DirectionType directionType = emitterNode.getDirectionType();
        final ParticleEmissionPoint emissionPoint = emitterNode.getParticleEmissionPoint();

        final ModelPropertyControl<ParticleEmitterNode, Boolean> testEmitterModeControl =
                new BooleanModelPropertyControl<>(enabledTestEmitter, Messages.PARTICLE_EMITTER_TEST_MODE, modelChangeConsumer);
        testEmitterModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestEmitter);
        testEmitterModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestEmitter);
        testEmitterModeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> enableControl =
                new BooleanModelPropertyControl<>(enabled, Messages.PARTICLE_EMITTER_ENABLED, modelChangeConsumer);
        enableControl.setApplyHandler(ParticleEmitterNode::setEnabled);
        enableControl.setSyncHandler(ParticleEmitterNode::isEnabled);
        enableControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> randomPointControl =
                new BooleanModelPropertyControl<>(randomEmissionPoint, Messages.PARTICLE_EMITTER_RANDOM_POINT, modelChangeConsumer);
        randomPointControl.setApplyHandler(ParticleEmitterNode::setRandomEmissionPoint);
        randomPointControl.setSyncHandler(ParticleEmitterNode::isRandomEmissionPoint);
        randomPointControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> sequentialFaceControl =
                new BooleanModelPropertyControl<>(sequentialEmissionFace, Messages.PARTICLE_EMITTER_SEQUENTIAL_FACE, modelChangeConsumer);
        sequentialFaceControl.setApplyHandler(ParticleEmitterNode::setSequentialEmissionFace);
        sequentialFaceControl.setSyncHandler(ParticleEmitterNode::isSequentialEmissionFace);
        sequentialFaceControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> skipPatternControl =
                new BooleanModelPropertyControl<>(skipPattern, Messages.PARTICLE_EMITTER_SKIP_PATTERN, modelChangeConsumer);
        skipPatternControl.setApplyHandler(ParticleEmitterNode::setSequentialSkipPattern);
        skipPatternControl.setSyncHandler(ParticleEmitterNode::isSequentialSkipPattern);
        skipPatternControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, DirectionType> directionTypeControl =
                new EnumModelPropertyControl<>(directionType, Messages.PARTICLE_EMITTER_DIRECTION_TYPE, modelChangeConsumer, DirectionType.values());
        directionTypeControl.setApplyHandler(ParticleEmitterNode::setDirectionType);
        directionTypeControl.setSyncHandler(ParticleEmitterNode::getDirectionType);
        directionTypeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, ParticleEmissionPoint> emissionPointControl =
                new EnumModelPropertyControl<>(emissionPoint, Messages.PARTICLE_EMITTER_EMISSION_POINT, modelChangeConsumer, ParticleEmissionPoint.values());
        emissionPointControl.setApplyHandler(ParticleEmitterNode::setParticleEmissionPoint);
        emissionPointControl.setSyncHandler(ParticleEmitterNode::getParticleEmissionPoint);
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

        final Line splitLine = createSplitLine(container);

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
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }
}
