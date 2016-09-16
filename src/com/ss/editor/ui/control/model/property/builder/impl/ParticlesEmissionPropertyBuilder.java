package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.BillboardEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.DirectionEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.EmissionPointEmissionPropertyControl;
import com.ss.editor.ui.control.model.property.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.IntegerModelPropertyControl;
import com.ss.editor.ui.control.model.property.MaterialModelPropertyEditor;
import com.ss.editor.ui.control.model.property.MinMaxModelPropertyControl;
import com.ss.editor.ui.control.model.property.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link
 * ParticleEmitterNode} objects.
 *
 * @author JavaSaBr
 */
public class ParticlesEmissionPropertyBuilder extends AbstractPropertyBuilder {

    private static final BiConsumer<ParticleEmitterNode, MaterialKey> MATERIAL_APPLY_TO_EMITTER_HANDLER = (emitter, materialKey) -> {

        final AssetManager assetManager = EDITOR.getAssetManager();

        if (materialKey == null) {

            final Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

            emitter.setMaterial(material);

        } else {

            assetManager.clearCache();

            final Material material = assetManager.loadAsset(materialKey);
            emitter.setMaterial(material);
        }
    };

    private static final Function<ParticleEmitterNode, MaterialKey> MATERIAL_FROM_EMITTER_SYNC_HANDLER = geometry -> {
        final Material material = geometry.getMaterial();
        return (MaterialKey) material.getKey();
    };


    private static final PropertyBuilder INSTANCE = new ParticlesEmissionPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    public void buildFor(@NotNull final Object object, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        if (!(object instanceof ParticleEmitterNode)) return;

        final ParticleEmitterNode emitterNode = (ParticleEmitterNode) object;

        createEmissionControls(container, modelChangeConsumer, emitterNode);
        createParticlesControls(container, modelChangeConsumer, emitterNode);
    }

    private void createParticlesControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final ParticleEmitterNode emitterNode) {

        final boolean testParticles = emitterNode.isEnabledTestParticles();
        final boolean particlesFollowEmitter = emitterNode.isParticlesFollowEmitter();

        final ParticleEmitterNode.BillboardMode billboardMode = emitterNode.getBillboardMode();

        final Vector2f forceMinMax = emitterNode.getForceMinMax();
        final Vector2f lifeMinMax = emitterNode.getLifeMinMax();

        final float stretchFactor = emitterNode.getVelocityStretchFactor();

        final Material material = emitterNode.getMaterial();
        final MaterialKey materialKey = (MaterialKey) material.getKey();

        final ModelPropertyControl<ParticleEmitterNode, MaterialKey> materialControl = new MaterialModelPropertyEditor<>(materialKey, Messages.MODEL_PROPERTY_MATERIAL, modelChangeConsumer);
        materialControl.setApplyHandler(MATERIAL_APPLY_TO_EMITTER_HANDLER);
        materialControl.setSyncHandler(MATERIAL_FROM_EMITTER_SYNC_HANDLER);
        materialControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> testParticlesModeControl = new BooleanModelPropertyControl<>(testParticles, "Test Particles Mode", modelChangeConsumer);
        testParticlesModeControl.setApplyHandler(ParticleEmitterNode::setEnabledTestParticles);
        testParticlesModeControl.setSyncHandler(ParticleEmitterNode::isEnabledTestParticles);
        testParticlesModeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> particlesFollowEmitControl = new BooleanModelPropertyControl<>(particlesFollowEmitter, "Particle Follow Emitter", modelChangeConsumer);
        particlesFollowEmitControl.setApplyHandler(ParticleEmitterNode::setParticlesFollowEmitter);
        particlesFollowEmitControl.setSyncHandler(ParticleEmitterNode::isParticlesFollowEmitter);
        particlesFollowEmitControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Boolean> particlesStretchingControl = new BooleanModelPropertyControl<>(particlesFollowEmitter, "Particle Stretching", modelChangeConsumer);
        particlesStretchingControl.setApplyHandler(ParticleEmitterNode::setUseVelocityStretching);
        particlesStretchingControl.setSyncHandler(ParticleEmitterNode::isUseVelocityStretching);
        particlesStretchingControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Float> magnitudeControl = new FloatModelPropertyControl<>(stretchFactor, "Magnitude", modelChangeConsumer);
        magnitudeControl.setApplyHandler(ParticleEmitterNode::setVelocityStretchFactor);
        magnitudeControl.setSyncHandler(ParticleEmitterNode::getVelocityStretchFactor);
        magnitudeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, ParticleEmitterNode.BillboardMode> billboardModeControl = new BillboardEmitterPropertyControl(billboardMode, "Billboard Mode", modelChangeConsumer);
        billboardModeControl.setApplyHandler(ParticleEmitterNode::setBillboardMode);
        billboardModeControl.setSyncHandler(ParticleEmitterNode::getBillboardMode);
        billboardModeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Vector2f> forceMinMaxControl = new MinMaxModelPropertyControl<>(forceMinMax, "Initial Force", modelChangeConsumer);
        forceMinMaxControl.setApplyHandler(ParticleEmitterNode::setForceMinMax);
        forceMinMaxControl.setSyncHandler(ParticleEmitterNode::getForceMinMax);
        forceMinMaxControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, Vector2f> lifeMinMaxControl = new MinMaxModelPropertyControl<>(lifeMinMax, "Particle Life", modelChangeConsumer);
        lifeMinMaxControl.setApplyHandler(ParticleEmitterNode::setLifeMinMax);
        lifeMinMaxControl.setSyncHandler(ParticleEmitterNode::getLifeMinMax);
        lifeMinMaxControl.setEditObject(emitterNode);

        final Line splitLine = createSplitLine(container);

        FXUtils.addToPane(materialControl, container);
        FXUtils.addToPane(testParticlesModeControl, container);
        FXUtils.addToPane(particlesFollowEmitControl, container);
        FXUtils.addToPane(particlesStretchingControl, container);
        FXUtils.addToPane(magnitudeControl, container);
        FXUtils.addToPane(billboardModeControl, container);
        FXUtils.addToPane(forceMinMaxControl, container);
        FXUtils.addToPane(lifeMinMaxControl, container);
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }

    private void createEmissionControls(@NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final ParticleEmitterNode emitterNode) {

        final boolean enabledTestEmitter = emitterNode.isEnabledTestEmitter();
        final boolean enabled = emitterNode.isEnabled();
        final boolean randomEmissionPoint = emitterNode.isUseRandomEmissionPoint();
        final boolean sequentialEmissionFace = emitterNode.isUseSequentialEmissionFace();
        final boolean skipPattern = emitterNode.isUseSequentialSkipPattern();

        final int maxParticles = emitterNode.getMaxParticles();
        final int emissionsPerSecond = emitterNode.getEmissionsPerSecond();
        final int particlesPerEmission = emitterNode.getParticlesPerEmission();

        final EmitterMesh.DirectionType directionType = emitterNode.getDirectionType();
        final ParticleEmitterNode.ParticleEmissionPoint emissionPoint = emitterNode.getParticleEmissionPoint();

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

        final ModelPropertyControl<ParticleEmitterNode, EmitterMesh.DirectionType> directionTypeControl = new DirectionEmitterPropertyControl(directionType, "Direction Type", modelChangeConsumer);
        directionTypeControl.setApplyHandler(ParticleEmitterNode::setDirectionType);
        directionTypeControl.setSyncHandler(ParticleEmitterNode::getDirectionType);
        directionTypeControl.setEditObject(emitterNode);

        final ModelPropertyControl<ParticleEmitterNode, ParticleEmitterNode.ParticleEmissionPoint> emissionPointControl = new EmissionPointEmissionPropertyControl(emissionPoint, "Direction Type", modelChangeConsumer);
        emissionPointControl.setApplyHandler(ParticleEmitterNode::setParticleEmissionPoint);
        emissionPointControl.setSyncHandler(ParticleEmitterNode::getParticleEmissionPoint);
        emissionPointControl.setEditObject(emitterNode);

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
        FXUtils.addToPane(directionTypeControl, container);
        FXUtils.addToPane(emissionPointControl, container);
        FXUtils.addToPane(maxParticlesControl, container);
        FXUtils.addToPane(emissionPerSecControl, container);
        FXUtils.addToPane(particlesPerEmissionControl, container);
        FXUtils.addToPane(splitLine, container);

        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
    }
}
