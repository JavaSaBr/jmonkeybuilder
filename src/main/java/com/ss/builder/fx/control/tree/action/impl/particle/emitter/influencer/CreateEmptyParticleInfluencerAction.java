package com.ss.builder.fx.control.tree.action.impl.particle.emitter.influencer;

import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create an {@link EmptyParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class CreateEmptyParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateEmptyParticleInfluencerAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull ParticleInfluencer createInfluencer() {
        return new EmptyParticleInfluencer();
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_EMPTY;
    }
}
