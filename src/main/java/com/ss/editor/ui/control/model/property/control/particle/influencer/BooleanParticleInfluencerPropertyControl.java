package com.ss.editor.ui.control.model.property.control.particle.influencer;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.BooleanPropertyControl;
import com.ss.editor.ui.control.property.impl.FloatPropertyControl;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link FloatPropertyControl} to edit boolean values in the {@link
 * ParticleInfluencer}*.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class BooleanParticleInfluencerPropertyControl<T extends ParticleInfluencer>
        extends BooleanPropertyControl<ModelChangeConsumer, T> {

    public BooleanParticleInfluencerPropertyControl(@NotNull final Boolean element, @NotNull final String paramName,
                                                    @NotNull final ModelChangeConsumer modelChangeConsumer,
                                                    @NotNull final Object parent) {
        super(element, paramName, modelChangeConsumer, ParticleInfluencerPropertyControl.newChangeHandler(parent));
    }
}
