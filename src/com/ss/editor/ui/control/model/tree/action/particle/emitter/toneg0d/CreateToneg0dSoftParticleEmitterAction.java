package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d;

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
public class CreateToneg0dSoftParticleEmitterAction extends CreateToneg0dParticleEmitterAction {

    /**
     * Instantiates a new Create toneg 0 d soft emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateToneg0dSoftParticleEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_SOFT_TONEG0D_PARTICLE_EMITTER;
    }

    @NotNull
    protected ParticleEmitterNode createEmitterNode() {
        return new SoftParticleEmitterNode(EDITOR.getAssetManager());
    }
}
