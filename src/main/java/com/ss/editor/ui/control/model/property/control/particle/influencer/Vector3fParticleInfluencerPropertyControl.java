package com.ss.editor.ui.control.model.property.control.particle.influencer;

import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.FloatPropertyControl;
import com.ss.editor.ui.control.property.impl.Vector3FPropertyControl;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link FloatPropertyControl} to edit vector3f values in the {@link
 * ParticleInfluencer}*.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class Vector3fParticleInfluencerPropertyControl<T extends ParticleInfluencer> extends
        Vector3FPropertyControl<ModelChangeConsumer, T> {

    public Vector3fParticleInfluencerPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName,
                                                     @NotNull final ModelChangeConsumer modelChangeConsumer,
                                                     @NotNull final Object parent) {
        super(element, paramName, modelChangeConsumer, ParticleInfluencerPropertyControl.newChangeHandler(parent));
    }
}
