package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.influerencer;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.SpriteInfluencer;

/**
 * The action to create a {@link SpriteInfluencer} for a {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateSpriteParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateSpriteParticleInfluencerAction(@NotNull final NodeTree<ModelChangeConsumer> nodeTree,
                                                @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_SPRITE;
    }

    @Override
    @FXThread
    protected @NotNull ParticleInfluencer createInfluencer() {
        return new SpriteInfluencer();
    }
}
