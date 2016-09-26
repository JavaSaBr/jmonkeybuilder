package com.ss.editor.ui.control.model.tree.action.emitter.influerencer;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.DestinationInfluencer;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action for creating the {@link DestinationInfluencer} for the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateDestinationParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateDestinationParticleInfluencerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Destination influencer";
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new DestinationInfluencer();
    }
}
