package com.ss.editor.model.node;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.util.array.Array;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * @author JavaSaBr
 */
public class ParticleInfluencers {

    /**
     * The emitter node.
     */
    @NotNull
    private final ParticleEmitterNode emitterNode;

    public ParticleInfluencers(@NotNull final ParticleEmitterNode emitterNode) {
        this.emitterNode = emitterNode;
    }

    /**
     * @return the emitter node.
     */
    @NotNull
    public ParticleEmitterNode getEmitterNode() {
        return emitterNode;
    }

    /**
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
        final ParticleInfluencers that = (ParticleInfluencers) o;
        return emitterNode.equals(that.emitterNode);
    }

    @Override
    public int hashCode() {
        return emitterNode.hashCode();
    }

    @Override
    public String toString() {
        return "ParticleInfluencers{" +
                "emitterNode=" + emitterNode +
                '}';
    }
}
