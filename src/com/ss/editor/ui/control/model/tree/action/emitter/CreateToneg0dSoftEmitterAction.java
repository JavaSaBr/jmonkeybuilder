package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.SoftParticleEmitterNode;

/**
 * The action for creating new {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateToneg0dSoftEmitterAction extends CreateToneg0dEmitterAction {

    public CreateToneg0dSoftEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_SOFT_TEMITTER;
    }

    @NotNull
    protected ParticleEmitterNode createEmitterNode() {
        return new SoftParticleEmitterNode(EDITOR.getAssetManager());
    }
}
