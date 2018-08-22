package com.ss.builder.fx.control.tree.node.impl.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ParticleInfluencerTreeNode} for representing the {@link DefaultParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class DefaultParticleInfluencerTreeNode extends ParticleInfluencerTreeNode {

    public DefaultParticleInfluencerTreeNode(@NotNull final DefaultParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_DEFAULT;
    }
}
