package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.model.property.particle.influencer.BooleanParticleInfluencerPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.FloatParticleInfluencerPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.GravityAlignmentEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.RadialPullAlignmentEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.RadialPullCenterEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.RadialUpAlignmentEmitterPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.Vector3fParticleInfluencerPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;
import tonegod.emitter.influencers.AlphaInfluencer;
import tonegod.emitter.influencers.ColorInfluencer;
import tonegod.emitter.influencers.DestinationInfluencer;
import tonegod.emitter.influencers.GravityInfluencer;
import tonegod.emitter.influencers.ImpulseInfluencer;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.RadialVelocityInfluencer;
import tonegod.emitter.influencers.SizeInfluencer;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link
 * ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencerPropertyBuilder extends AbstractPropertyBuilder {

    private static final PropertyBuilder INSTANCE = new ParticleInfluencerPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        if (!(object instanceof ParticleInfluencer) || parent == null) return;

        if (object instanceof AlphaInfluencer) {
            createControls(container, modelChangeConsumer, (AlphaInfluencer) object, parent);
        } else if (object instanceof ColorInfluencer) {
            createControls(container, modelChangeConsumer, (ColorInfluencer) object, parent);
        } else if (object instanceof SizeInfluencer) {
            createControls(container, modelChangeConsumer, (SizeInfluencer) object, parent);
        } else if (object instanceof DestinationInfluencer) {
            createControls(container, modelChangeConsumer, (DestinationInfluencer) object, parent);
        } else if (object instanceof ImpulseInfluencer) {
            createControls(container, modelChangeConsumer, (ImpulseInfluencer) object, parent);
        } else if (object instanceof GravityInfluencer) {
            createControls(container, modelChangeConsumer, (GravityInfluencer) object, parent);
        } else if (object instanceof RadialVelocityInfluencer) {
            createControls(container, modelChangeConsumer, (RadialVelocityInfluencer) object, parent);
        }
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final AlphaInfluencer influencer, @NotNull final Object parent) {

        final FloatParticleInfluencerPropertyControl<AlphaInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(0F, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(AlphaInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(AlphaInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(influencer);

        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final ColorInfluencer influencer, @NotNull final Object parent) {

        final float fixedDuration = influencer.getFixedDuration();

        final boolean randomStartColor = influencer.isRandomStartColor();

        final BooleanParticleInfluencerPropertyControl<ColorInfluencer> randomStartColorControl = new BooleanParticleInfluencerPropertyControl<>(randomStartColor, "Random color", modelChangeConsumer, parent);
        randomStartColorControl.setSyncHandler(ColorInfluencer::isRandomStartColor);
        randomStartColorControl.setApplyHandler(ColorInfluencer::setRandomStartColor);
        randomStartColorControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<ColorInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(fixedDuration, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(ColorInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(ColorInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(influencer);

        FXUtils.addToPane(randomStartColorControl, container);
        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final SizeInfluencer influencer, @NotNull final Object parent) {

        final boolean randomSize = influencer.isRandomSize();

        final float randomSizeTolerance = influencer.getRandomSizeTolerance();
        final float fixedDuration = influencer.getFixedDuration();

        final BooleanParticleInfluencerPropertyControl<SizeInfluencer> randomStartSizeControl = new BooleanParticleInfluencerPropertyControl<>(randomSize, "Random size", modelChangeConsumer, parent);
        randomStartSizeControl.setSyncHandler(SizeInfluencer::isRandomSize);
        randomStartSizeControl.setApplyHandler(SizeInfluencer::setRandomSize);
        randomStartSizeControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<SizeInfluencer> sizeVariationTolereControl = new FloatParticleInfluencerPropertyControl<>(randomSizeTolerance, "Size variation tolere", modelChangeConsumer, parent);
        sizeVariationTolereControl.setSyncHandler(SizeInfluencer::getRandomSizeTolerance);
        sizeVariationTolereControl.setApplyHandler(SizeInfluencer::setRandomSizeTolerance);
        sizeVariationTolereControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<SizeInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(fixedDuration, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(SizeInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(SizeInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(influencer);

        FXUtils.addToPane(randomStartSizeControl, container);
        FXUtils.addToPane(sizeVariationTolereControl, container);
        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final DestinationInfluencer influencer, @NotNull final Object parent) {

        final float fixedDuration = influencer.getFixedDuration();

        final boolean randomStartDestination = influencer.isRandomStartDestination();

        final BooleanParticleInfluencerPropertyControl<DestinationInfluencer> randomStartDestinationControl = new BooleanParticleInfluencerPropertyControl<>(randomStartDestination, "Random destination", modelChangeConsumer, parent);
        randomStartDestinationControl.setSyncHandler(DestinationInfluencer::isRandomStartDestination);
        randomStartDestinationControl.setApplyHandler(DestinationInfluencer::setRandomStartDestination);
        randomStartDestinationControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<DestinationInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(fixedDuration, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(DestinationInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(DestinationInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(influencer);

        FXUtils.addToPane(randomStartDestinationControl, container);
        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final ImpulseInfluencer influencer, @NotNull final Object parent) {

        final float chance = influencer.getChance();
        final float strength = influencer.getStrength();
        final float magnitude = influencer.getMagnitude();

        final FloatParticleInfluencerPropertyControl<ImpulseInfluencer> chanceControl = new FloatParticleInfluencerPropertyControl<>(chance, "Chance", modelChangeConsumer, parent);
        chanceControl.setSyncHandler(ImpulseInfluencer::getChance);
        chanceControl.setApplyHandler(ImpulseInfluencer::setChance);
        chanceControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<ImpulseInfluencer> strengthControl = new FloatParticleInfluencerPropertyControl<>(strength, "Strength", modelChangeConsumer, parent);
        strengthControl.setSyncHandler(ImpulseInfluencer::getStrength);
        strengthControl.setApplyHandler(ImpulseInfluencer::setStrength);
        strengthControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<ImpulseInfluencer> magnitudeControl = new FloatParticleInfluencerPropertyControl<>(magnitude, "Magnitude", modelChangeConsumer, parent);
        magnitudeControl.setSyncHandler(ImpulseInfluencer::getMagnitude);
        magnitudeControl.setApplyHandler(ImpulseInfluencer::setMagnitude);
        magnitudeControl.setEditObject(influencer);

        FXUtils.addToPane(chanceControl, container);
        FXUtils.addToPane(strengthControl, container);
        FXUtils.addToPane(magnitudeControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final GravityInfluencer influencer, @NotNull final Object parent) {

        final Vector3f gravity = influencer.getGravity().clone();
        final GravityInfluencer.GravityAlignment alignment = influencer.getAlignment();

        final float magnitude = influencer.getMagnitude();

        final Vector3fParticleInfluencerPropertyControl<GravityInfluencer> gravityControl = new Vector3fParticleInfluencerPropertyControl<>(gravity, "Gravity", modelChangeConsumer, parent);
        gravityControl.setSyncHandler(GravityInfluencer::getGravity);
        gravityControl.setApplyHandler(GravityInfluencer::setGravity);
        gravityControl.setEditObject(influencer);

        final GravityAlignmentEmitterPropertyControl<GravityInfluencer> gravityAlignmentControl = new GravityAlignmentEmitterPropertyControl<>(alignment, "Alignment", modelChangeConsumer, parent);
        gravityAlignmentControl.setSyncHandler(GravityInfluencer::getAlignment);
        gravityAlignmentControl.setApplyHandler(GravityInfluencer::setAlignment);
        gravityAlignmentControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<GravityInfluencer> magnitudeControl = new FloatParticleInfluencerPropertyControl<>(magnitude, "Magnitude", modelChangeConsumer, parent);
        magnitudeControl.setSyncHandler(GravityInfluencer::getMagnitude);
        magnitudeControl.setApplyHandler(GravityInfluencer::setMagnitude);
        magnitudeControl.setEditObject(influencer);

        FXUtils.addToPane(gravityControl, container);

        addLine(container);

        FXUtils.addToPane(gravityAlignmentControl, container);
        FXUtils.addToPane(magnitudeControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final RadialVelocityInfluencer influencer, @NotNull final Object parent) {

        final RadialVelocityInfluencer.RadialPullCenter pullCenter = influencer.getRadialPullCenter();
        final RadialVelocityInfluencer.RadialPullAlignment pullAlignment = influencer.getRadialPullAlignment();
        final RadialVelocityInfluencer.RadialUpAlignment upAlignment = influencer.getRadialUpAlignment();

        final float tangentForce = influencer.getTangentForce();
        final float radialPull = influencer.getRadialPull();

        final boolean randomDirection = influencer.isRandomDirection();

        final BooleanParticleInfluencerPropertyControl<RadialVelocityInfluencer> randomDirectionControl = new BooleanParticleInfluencerPropertyControl<>(randomDirection, "Random destination", modelChangeConsumer, parent);
        randomDirectionControl.setSyncHandler(RadialVelocityInfluencer::isRandomDirection);
        randomDirectionControl.setApplyHandler(RadialVelocityInfluencer::setRandomDirection);
        randomDirectionControl.setEditObject(influencer);

        final RadialPullCenterEmitterPropertyControl<RadialVelocityInfluencer> pullCenterControl = new RadialPullCenterEmitterPropertyControl<>(pullCenter, "Pull center", modelChangeConsumer, parent);
        pullCenterControl.setSyncHandler(RadialVelocityInfluencer::getRadialPullCenter);
        pullCenterControl.setApplyHandler(RadialVelocityInfluencer::setRadialPullCenter);
        pullCenterControl.setEditObject(influencer);

        final RadialPullAlignmentEmitterPropertyControl<RadialVelocityInfluencer> pullAlignmentControl = new RadialPullAlignmentEmitterPropertyControl<>(pullAlignment, "Pull alignment", modelChangeConsumer, parent);
        pullAlignmentControl.setSyncHandler(RadialVelocityInfluencer::getRadialPullAlignment);
        pullAlignmentControl.setApplyHandler(RadialVelocityInfluencer::setRadialPullAlignment);
        pullAlignmentControl.setEditObject(influencer);

        final RadialUpAlignmentEmitterPropertyControl<RadialVelocityInfluencer> upAlignmentControl = new RadialUpAlignmentEmitterPropertyControl<>(upAlignment, "Up alignment", modelChangeConsumer, parent);
        upAlignmentControl.setSyncHandler(RadialVelocityInfluencer::getRadialUpAlignment);
        upAlignmentControl.setApplyHandler(RadialVelocityInfluencer::setRadialUpAlignment);
        upAlignmentControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<RadialVelocityInfluencer> radialPullControl = new FloatParticleInfluencerPropertyControl<>(radialPull, "Radial pull", modelChangeConsumer, parent);
        radialPullControl.setSyncHandler(RadialVelocityInfluencer::getRadialPull);
        radialPullControl.setApplyHandler(RadialVelocityInfluencer::setRadialPull);
        radialPullControl.setEditObject(influencer);

        final FloatParticleInfluencerPropertyControl<RadialVelocityInfluencer> tangetForceControl = new FloatParticleInfluencerPropertyControl<>(tangentForce, "Tanget force", modelChangeConsumer, parent);
        tangetForceControl.setSyncHandler(RadialVelocityInfluencer::getTangentForce);
        tangetForceControl.setApplyHandler(RadialVelocityInfluencer::setTangentForce);
        tangetForceControl.setEditObject(influencer);

        FXUtils.addToPane(randomDirectionControl, container);
        FXUtils.addToPane(pullCenterControl, container);
        FXUtils.addToPane(pullAlignmentControl, container);
        FXUtils.addToPane(upAlignmentControl, container);
        FXUtils.addToPane(radialPullControl, container);
        FXUtils.addToPane(tangetForceControl, container);
    }

    private void addLine(final @NotNull VBox container) {
        final Line splitLine = createSplitLine(container);
        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
        FXUtils.addToPane(splitLine, container);
    }
}
