package com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.DefaultParticleInfluencer;
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
        return "Default influencer";
    }
}
