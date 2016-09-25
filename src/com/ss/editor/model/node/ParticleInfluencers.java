package com.ss.editor.model.node;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * @author JavaSaBr
 */
public class ParticleInfluencers {

    /**
     * The emitter node.
     */
    private final ParticleEmitterNode emitterNode;

    public ParticleInfluencers(final ParticleEmitterNode emitterNode) {
        this.emitterNode = emitterNode;
    }

    /**
     * @return the emitter node.
     */
    private ParticleEmitterNode getEmitterNode() {
        return emitterNode;
    }

    /**
     * @return the array of influencers.
     */
    public ParticleInfluencer[] getInfluencers() {
        return emitterNode.getInfluencers();
    }
}
