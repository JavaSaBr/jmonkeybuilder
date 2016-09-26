package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.RotationInfluencer;

/**
 * The action for creating the {@link RotationInfluencer} for the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateRotationParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateRotationParticleInfluencerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Rotation influencer";
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new RotationInfluencer();
    }
}
