package com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer;

import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
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
    @FXThread
    protected @NotNull ParticleInfluencer createInfluencer() {
        return new DefaultParticleInfluencer();
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_DEFAULT;
    }
}
