package com.ss.editor.ui.control.model.property.control.particle.influencer;

import static com.ss.editor.ui.control.model.property.control.particle.influencer.ParticleInfluencerPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractBooleanPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractFloatPropertyControl;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractFloatPropertyControl} to edit boolean values in the {@link
 * ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class BooleanParticleInfluencerPropertyControl<T extends ParticleInfluencer>
        extends AbstractBooleanPropertyControl<ModelChangeConsumer, T> {

    public BooleanParticleInfluencerPropertyControl(@NotNull final Boolean element, @NotNull final String paramName,
                                                    @NotNull final ModelChangeConsumer modelChangeConsumer,
                                                    @NotNull final Object parent) {
        super(element, paramName, modelChangeConsumer, newChangeHandler(parent));
    }
}
