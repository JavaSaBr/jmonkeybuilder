package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.model.property.particle.influencer.BooleanParticleInfluencerPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.FloatParticleInfluencerPropertyControl;
import com.ss.editor.ui.control.model.property.particle.influencer.GravityAlignmentEmitterPropertyControl;
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
        }
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final AlphaInfluencer alphaInfluencer, @NotNull final Object parent) {

        final FloatParticleInfluencerPropertyControl<AlphaInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(0F, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(AlphaInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(AlphaInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(alphaInfluencer);

        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final ColorInfluencer colorInfluencer, @NotNull final Object parent) {

        final float fixedDuration = colorInfluencer.getFixedDuration();

        final boolean randomStartColor = colorInfluencer.isRandomStartColor();

        final BooleanParticleInfluencerPropertyControl<ColorInfluencer> randomStartColorControl = new BooleanParticleInfluencerPropertyControl<>(randomStartColor, "Random color", modelChangeConsumer, parent);
        randomStartColorControl.setSyncHandler(ColorInfluencer::isRandomStartColor);
        randomStartColorControl.setApplyHandler(ColorInfluencer::setRandomStartColor);
        randomStartColorControl.setEditObject(colorInfluencer);

        final FloatParticleInfluencerPropertyControl<ColorInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(fixedDuration, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(ColorInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(ColorInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(colorInfluencer);

        FXUtils.addToPane(randomStartColorControl, container);
        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final SizeInfluencer sizeInfluencer, @NotNull final Object parent) {

        final boolean randomSize = sizeInfluencer.isRandomSize();

        final float randomSizeTolerance = sizeInfluencer.getRandomSizeTolerance();
        final float fixedDuration = sizeInfluencer.getFixedDuration();

        final BooleanParticleInfluencerPropertyControl<SizeInfluencer> randomStartSizeControl = new BooleanParticleInfluencerPropertyControl<>(randomSize, "Random size", modelChangeConsumer, parent);
        randomStartSizeControl.setSyncHandler(SizeInfluencer::isRandomSize);
        randomStartSizeControl.setApplyHandler(SizeInfluencer::setRandomSize);
        randomStartSizeControl.setEditObject(sizeInfluencer);

        final FloatParticleInfluencerPropertyControl<SizeInfluencer> sizeVariationTolereControl = new FloatParticleInfluencerPropertyControl<>(randomSizeTolerance, "Size variation tolere", modelChangeConsumer, parent);
        sizeVariationTolereControl.setSyncHandler(SizeInfluencer::getRandomSizeTolerance);
        sizeVariationTolereControl.setApplyHandler(SizeInfluencer::setRandomSizeTolerance);
        sizeVariationTolereControl.setEditObject(sizeInfluencer);

        final FloatParticleInfluencerPropertyControl<SizeInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(fixedDuration, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(SizeInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(SizeInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(sizeInfluencer);

        FXUtils.addToPane(randomStartSizeControl, container);
        FXUtils.addToPane(sizeVariationTolereControl, container);
        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final DestinationInfluencer destinationInfluencer, @NotNull final Object parent) {

        final float fixedDuration = destinationInfluencer.getFixedDuration();

        final boolean randomStartDestination = destinationInfluencer.isRandomStartDestination();

        final BooleanParticleInfluencerPropertyControl<DestinationInfluencer> randomStartDestinationControl = new BooleanParticleInfluencerPropertyControl<>(randomStartDestination, "Random destination", modelChangeConsumer, parent);
        randomStartDestinationControl.setSyncHandler(DestinationInfluencer::isRandomStartDestination);
        randomStartDestinationControl.setApplyHandler(DestinationInfluencer::setRandomStartDestination);
        randomStartDestinationControl.setEditObject(destinationInfluencer);

        final FloatParticleInfluencerPropertyControl<DestinationInfluencer> fixedDurationControl = new FloatParticleInfluencerPropertyControl<>(fixedDuration, "Fixed duration", modelChangeConsumer, parent);
        fixedDurationControl.setSyncHandler(DestinationInfluencer::getFixedDuration);
        fixedDurationControl.setApplyHandler(DestinationInfluencer::setFixedDuration);
        fixedDurationControl.setEditObject(destinationInfluencer);

        FXUtils.addToPane(randomStartDestinationControl, container);
        FXUtils.addToPane(fixedDurationControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final ImpulseInfluencer impulseInfluencer, @NotNull final Object parent) {

        final float chance = impulseInfluencer.getChance();
        final float strength = impulseInfluencer.getStrength();
        final float magnitude = impulseInfluencer.getMagnitude();

        final FloatParticleInfluencerPropertyControl<ImpulseInfluencer> chanceControl = new FloatParticleInfluencerPropertyControl<>(chance, "Chance", modelChangeConsumer, parent);
        chanceControl.setSyncHandler(ImpulseInfluencer::getChance);
        chanceControl.setApplyHandler(ImpulseInfluencer::setChance);
        chanceControl.setEditObject(impulseInfluencer);

        final FloatParticleInfluencerPropertyControl<ImpulseInfluencer> strengthControl = new FloatParticleInfluencerPropertyControl<>(strength, "Strength", modelChangeConsumer, parent);
        strengthControl.setSyncHandler(ImpulseInfluencer::getStrength);
        strengthControl.setApplyHandler(ImpulseInfluencer::setStrength);
        strengthControl.setEditObject(impulseInfluencer);

        final FloatParticleInfluencerPropertyControl<ImpulseInfluencer> magnitudeControl = new FloatParticleInfluencerPropertyControl<>(magnitude, "Magnitude", modelChangeConsumer, parent);
        magnitudeControl.setSyncHandler(ImpulseInfluencer::getMagnitude);
        magnitudeControl.setApplyHandler(ImpulseInfluencer::setMagnitude);
        magnitudeControl.setEditObject(impulseInfluencer);

        FXUtils.addToPane(chanceControl, container);
        FXUtils.addToPane(strengthControl, container);
        FXUtils.addToPane(magnitudeControl, container);
    }

    protected void createControls(final @NotNull VBox container, final @NotNull ModelChangeConsumer modelChangeConsumer, @NotNull final GravityInfluencer gravityInfluencer, @NotNull final Object parent) {

        final Vector3f gravity = gravityInfluencer.getGravity().clone();
        final GravityInfluencer.GravityAlignment alignment = gravityInfluencer.getAlignment();

        final float magnitude = gravityInfluencer.getMagnitude();

        final Vector3fParticleInfluencerPropertyControl<GravityInfluencer> gravityControl = new Vector3fParticleInfluencerPropertyControl<>(gravity, "Gravity", modelChangeConsumer, parent);
        gravityControl.setSyncHandler(GravityInfluencer::getGravity);
        gravityControl.setApplyHandler(GravityInfluencer::setGravity);
        gravityControl.setEditObject(gravityInfluencer);

        final GravityAlignmentEmitterPropertyControl<GravityInfluencer> gravityAlignmentControl = new GravityAlignmentEmitterPropertyControl<>(alignment, "Aligment", modelChangeConsumer, parent);
        gravityAlignmentControl.setSyncHandler(GravityInfluencer::getAlignment);
        gravityAlignmentControl.setApplyHandler(GravityInfluencer::setAlignment);
        gravityAlignmentControl.setEditObject(gravityInfluencer);

        final FloatParticleInfluencerPropertyControl<GravityInfluencer> magnitudeControl = new FloatParticleInfluencerPropertyControl<>(magnitude, "Magnitude", modelChangeConsumer, parent);
        magnitudeControl.setSyncHandler(GravityInfluencer::getMagnitude);
        magnitudeControl.setApplyHandler(GravityInfluencer::setMagnitude);
        magnitudeControl.setEditObject(gravityInfluencer);

        FXUtils.addToPane(gravityControl, container);

        addLine(container);

        FXUtils.addToPane(gravityAlignmentControl, container);
        FXUtils.addToPane(magnitudeControl, container);
    }

    private void addLine(final @NotNull VBox container) {
        final Line splitLine = createSplitLine(container);
        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
        FXUtils.addToPane(splitLine, container);
    }
}
