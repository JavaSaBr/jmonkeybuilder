package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencerPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new ParticleInfluencerPropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private ParticleInfluencerPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof ParticleInfluencer)) return;

        final ParticleInfluencer influencer = (ParticleInfluencer) object;
        final Vector3f initialVelocity = influencer.getInitialVelocity();

        final float velocityVariation = influencer.getVelocityVariation();

        final Vector3fModelPropertyControl<ParticleInfluencer> initialVelocityControl =
                new Vector3fModelPropertyControl<>(initialVelocity, Messages.MODEL_PROPERTY_INITIAL_VELOCITY, changeConsumer);

        initialVelocityControl.setSyncHandler(ParticleInfluencer::getInitialVelocity);
        initialVelocityControl.setApplyHandler(ParticleInfluencer::setInitialVelocity);
        initialVelocityControl.setEditObject(influencer);

        FXUtils.addToPane(initialVelocityControl, container);

        if (object instanceof RadialParticleInfluencer) {
            createControls(container, changeConsumer, (RadialParticleInfluencer) object);
        } else {
            buildSplitLine(initialVelocityControl);
        }

        if (influencer instanceof EmptyParticleInfluencer) {
            initialVelocityControl.setDisable(true);
        }

        final FloatModelPropertyControl<ParticleInfluencer> velocityVariationControl =
                new FloatModelPropertyControl<>(velocityVariation, Messages.MODEL_PROPERTY_VELOCITY_VARIATION, changeConsumer);

        velocityVariationControl.setSyncHandler(ParticleInfluencer::getVelocityVariation);
        velocityVariationControl.setApplyHandler(ParticleInfluencer::setVelocityVariation);
        velocityVariationControl.setEditObject(influencer);

        FXUtils.addToPane(velocityVariationControl, container);
    }

    /**
     * Create controls.
     *
     * @param container      the container
     * @param changeConsumer the change consumer
     * @param influencer     the influencer
     */
    private void createControls(@NotNull final VBox container, final @NotNull ModelChangeConsumer changeConsumer,
                                @NotNull final RadialParticleInfluencer influencer) {

        final Vector3f origin = influencer.getOrigin();
        final float radialVelocity = influencer.getRadialVelocity();
        final boolean horizontal = influencer.isHorizontal();

        final FloatModelPropertyControl<RadialParticleInfluencer> radialVelocityControl =
                new FloatModelPropertyControl<>(radialVelocity, Messages.MODEL_PROPERTY_RADIAL_VELOCITY, changeConsumer);

        radialVelocityControl.setSyncHandler(RadialParticleInfluencer::getRadialVelocity);
        radialVelocityControl.setApplyHandler(RadialParticleInfluencer::setRadialVelocity);
        radialVelocityControl.setEditObject(influencer);

        final BooleanModelPropertyControl<RadialParticleInfluencer> horizontalControl =
                new BooleanModelPropertyControl<>(horizontal, Messages.MODEL_PROPERTY_IS_HORIZONTAL, changeConsumer);

        horizontalControl.setSyncHandler(RadialParticleInfluencer::isHorizontal);
        horizontalControl.setApplyHandler(RadialParticleInfluencer::setHorizontal);
        horizontalControl.setEditObject(influencer);

        final Vector3fModelPropertyControl<RadialParticleInfluencer> originControl =
                new Vector3fModelPropertyControl<>(origin, Messages.MODEL_PROPERTY_ORIGIN, changeConsumer);

        originControl.setSyncHandler(RadialParticleInfluencer::getOrigin);
        originControl.setApplyHandler(RadialParticleInfluencer::setOrigin);
        originControl.setEditObject(influencer);

        FXUtils.addToPane(originControl, container);
        buildSplitLine(container);
        FXUtils.addToPane(radialVelocityControl, container);
        FXUtils.addToPane(horizontalControl, container);
    }
}
