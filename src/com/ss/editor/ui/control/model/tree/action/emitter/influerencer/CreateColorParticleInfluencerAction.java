package com.ss.editor.ui.control.model.tree.action.emitter.influerencer;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.ColorInfluencer;

/**
 * The action to create a {@link ColorInfluencer} for a {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateColorParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateColorParticleInfluencerAction(@NotNull final AbstractNodeTree<ModelChangeConsumer> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CREATE_INFLUENCER_COLOR;
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new ColorInfluencer();
    }
}
