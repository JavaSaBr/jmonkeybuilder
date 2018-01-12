package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.SoftParticleEmitterNode;

/**
 * The action for creating new {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateToneg0dSoftParticleEmitterAction extends CreateToneg0dParticleEmitterAction {

    public CreateToneg0dSoftParticleEmitterAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_SOFT_TONEG0D_PARTICLE_EMITTER;
    }

    @Override
    @FXThread
    protected @NotNull ParticleEmitterNode createEmitterNode() {
        return new SoftParticleEmitterNode(JME_APPLICATION.getAssetManager());
    }
}
