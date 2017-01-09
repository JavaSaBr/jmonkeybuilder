package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.SoftParticleEmitterNode;

/**
 * The action for creating new {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateTonegodSoftEmitterAction extends CreateTonegodEmitterAction {

    public CreateTonegodSoftEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
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
