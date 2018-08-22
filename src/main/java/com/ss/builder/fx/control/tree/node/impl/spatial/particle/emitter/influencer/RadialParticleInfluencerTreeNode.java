package com.ss.builder.ui.control.tree.node.impl.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ParticleInfluencerTreeNode} for representing the {@link RadialParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class RadialParticleInfluencerTreeNode extends ParticleInfluencerTreeNode {

    public RadialParticleInfluencerTreeNode(@NotNull final RadialParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_RADIAL;
    }
}
