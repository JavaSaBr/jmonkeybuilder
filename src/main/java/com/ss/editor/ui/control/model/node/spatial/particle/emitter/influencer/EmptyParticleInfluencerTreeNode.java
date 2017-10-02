package com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ParticleInfluencerTreeNode} for representing the {@link EmptyParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class EmptyParticleInfluencerTreeNode extends ParticleInfluencerTreeNode {

    public EmptyParticleInfluencerTreeNode(@NotNull final EmptyParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCER_EMPTY;
    }
}
