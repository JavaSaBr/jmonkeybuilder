package com.ss.builder.fx.control.tree.action.impl.particle.emitter.influencer;

import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create an {@link DefaultParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class CreateDefaultParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateDefaultParticleInfluencerAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull ParticleInfluencer createInfluencer() {
        return new DefaultParticleInfluencer();
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_DEFAULT;
    }
}
