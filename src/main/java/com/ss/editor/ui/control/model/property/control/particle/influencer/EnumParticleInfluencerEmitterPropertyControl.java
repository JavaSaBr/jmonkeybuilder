package com.ss.editor.ui.control.model.property.control.particle.influencer;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.EnumPropertyControl;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link EnumPropertyControl} to edit the {@link Enum} values.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @author JavaSaBr
 */
public class EnumParticleInfluencerEmitterPropertyControl<T extends ParticleInfluencer, E extends Enum<E>>
        extends EnumPropertyControl<ModelChangeConsumer, T, E> {

    public EnumParticleInfluencerEmitterPropertyControl(@NotNull final E element, @NotNull final String paramName,
                                                        @NotNull final ModelChangeConsumer modelChangeConsumer,
                                                        @NotNull final E[] availableValues, final @NotNull Object parent) {
        super(element, paramName, modelChangeConsumer, availableValues, ParticleInfluencerPropertyControl.newChangeHandler(parent));
    }
}
