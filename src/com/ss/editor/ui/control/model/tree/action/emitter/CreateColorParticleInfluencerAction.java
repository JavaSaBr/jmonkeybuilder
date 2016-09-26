package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ColorInfluencer;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action for creating the {@link ColorInfluencer} for the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateColorParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateColorParticleInfluencerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Color influencer";
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new ColorInfluencer();
    }
}
