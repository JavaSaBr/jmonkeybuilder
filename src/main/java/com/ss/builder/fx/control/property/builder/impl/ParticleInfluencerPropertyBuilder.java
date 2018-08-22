package com.ss.builder.ui.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.SeparatorProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencerPropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final PropertyBuilder INSTANCE = new ParticleInfluencerPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private ParticleInfluencerPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!(object instanceof ParticleInfluencer)) {
            return null;
        }

        var influencer = (ParticleInfluencer) object;
        var properties = new ArrayList<EditableProperty<?, ?>>();

        if (influencer instanceof EmptyParticleInfluencer) {
            properties.add(new SimpleProperty<>(READ_ONLY_STRING, Messages.MODEL_PROPERTY_INITIAL_VELOCITY,
                    influencer.getInitialVelocity(), String::valueOf));
        } else {
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_INITIAL_VELOCITY, influencer,
                    ParticleInfluencer::getInitialVelocity, ParticleInfluencer::setInitialVelocity));
        }

        if (object instanceof RadialParticleInfluencer) {

            var radialParticleInfluencer = (RadialParticleInfluencer) object;

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_ORIGIN, radialParticleInfluencer,
                    RadialParticleInfluencer::getOrigin, RadialParticleInfluencer::setOrigin));

            properties.add(SeparatorProperty.getInstance());

            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_RADIAL_VELOCITY, radialParticleInfluencer,
                    RadialParticleInfluencer::getRadialVelocity, RadialParticleInfluencer::setRadialVelocity));
            properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_HORIZONTAL, radialParticleInfluencer,
                    RadialParticleInfluencer::isHorizontal, RadialParticleInfluencer::setHorizontal));

        } else {
            properties.add(SeparatorProperty.getInstance());
        }

        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_VELOCITY_VARIATION, influencer,
                ParticleInfluencer::getVelocityVariation, ParticleInfluencer::setVelocityVariation));

        return properties;
    }
}
