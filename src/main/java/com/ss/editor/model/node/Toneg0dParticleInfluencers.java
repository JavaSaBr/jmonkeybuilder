package com.ss.editor.model.node;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.util.array.Array;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The type Particle influencers.
 *
 * @author JavaSaBr
 */
public class Toneg0dParticleInfluencers {

    /**
     * The emitter node.
     */
    @NotNull
    private final ParticleEmitterNode emitterNode;

    /**
     * Instantiates a new Particle influencers.
     *
     * @param emitterNode the emitter node
     */
    public Toneg0dParticleInfluencers(@NotNull final ParticleEmitterNode emitterNode) {
        this.emitterNode = emitterNode;
    }

    /**
     * Gets emitter node.
     *
     * @return the emitter node.
     */
    @NotNull
    public ParticleEmitterNode getEmitterNode() {
        return emitterNode;
    }

    /**
     * Gets influencers.
     *
     * @return the array of influencers.
     */
    @NotNull
    public Array<ParticleInfluencer> getInfluencers() {
        return emitterNode.getInfluencers();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Toneg0dParticleInfluencers that = (Toneg0dParticleInfluencers) o;
        return emitterNode.equals(that.emitterNode);
    }

    @Override
    public int hashCode() {
        return emitterNode.hashCode();
    }

    @Override
    public String toString() {
        return "Toneg0dParticleInfluencers{" +
                "emitterNode=" + emitterNode +
                '}';
    }
}
