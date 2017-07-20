package com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer;

import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create an {@link DefaultParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class CreateDefaultParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    /**
     * Instantiates a new Create default particle influencer action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateDefaultParticleInfluencerAction(@NotNull final NodeTree<?> nodeTree,
                                                 @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new DefaultParticleInfluencer();
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_DEFAULT;
    }
}
