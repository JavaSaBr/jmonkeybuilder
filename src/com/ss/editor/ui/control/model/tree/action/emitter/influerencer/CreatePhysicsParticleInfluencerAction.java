package com.ss.editor.ui.control.model.tree.action.emitter.influerencer;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.PhysicsInfluencer;

/**
 * The action for creating the {@link PhysicsInfluencer} for the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreatePhysicsParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreatePhysicsParticleInfluencerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Physics influencer";
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new PhysicsInfluencer();
    }
}
