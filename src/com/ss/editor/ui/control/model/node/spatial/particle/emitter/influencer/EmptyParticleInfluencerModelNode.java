package com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.ss.editor.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ParticleInfluencerModelNode} for representing the {@link EmptyParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class EmptyParticleInfluencerModelNode extends ParticleInfluencerModelNode {

    /**
     * Instantiates a new Empty particle influencer model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public EmptyParticleInfluencerModelNode(@NotNull final EmptyParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_EMPTY;
    }
}
