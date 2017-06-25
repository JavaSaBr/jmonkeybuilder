package com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.RadialParticleInfluencer;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ParticleInfluencerModelNode} for representing the {@link RadialParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class RadialParticleInfluencerModelNode extends ParticleInfluencerModelNode {

    /**
     * Instantiates a new Radial particle influencer model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public RadialParticleInfluencerModelNode(@NotNull final RadialParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return "Radial influencer";
    }
}
