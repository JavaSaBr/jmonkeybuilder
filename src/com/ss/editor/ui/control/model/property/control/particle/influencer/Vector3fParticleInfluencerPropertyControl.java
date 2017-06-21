package com.ss.editor.ui.control.model.property.control.particle.influencer;

import static com.ss.editor.ui.control.model.property.control.particle.influencer.ParticleInfluencerPropertyControl.newChangeHandler;

import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractFloatPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractVector3fPropertyControl;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractFloatPropertyControl} to edit vector3f values in the {@link
 * ParticleInfluencer}*.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class Vector3fParticleInfluencerPropertyControl<T extends ParticleInfluencer>
        extends AbstractVector3fPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Vector 3 f particle influencer property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     * @param parent              the parent
     */
    public Vector3fParticleInfluencerPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName,
                                                     @NotNull final ModelChangeConsumer modelChangeConsumer,
                                                     @NotNull final Object parent) {
        super(element, paramName, modelChangeConsumer, newChangeHandler(parent));
    }
}
