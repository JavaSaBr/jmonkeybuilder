package com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer;

import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create an {@link EmptyParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class CreateRadialParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    /**
     * Instantiates a new Create radial particle influencer action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateRadialParticleInfluencerAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                                @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new RadialParticleInfluencer();
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_INFLUENCER_RADIAL;
    }
}
