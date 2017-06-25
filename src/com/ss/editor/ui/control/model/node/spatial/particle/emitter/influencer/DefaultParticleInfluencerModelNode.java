package com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.ss.editor.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ParticleInfluencerModelNode} for representing the {@link DefaultParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class DefaultParticleInfluencerModelNode extends ParticleInfluencerModelNode {

    /**
     * Instantiates a new Default particle influencer model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public DefaultParticleInfluencerModelNode(@NotNull final DefaultParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_DEFAULT;
    }
}
