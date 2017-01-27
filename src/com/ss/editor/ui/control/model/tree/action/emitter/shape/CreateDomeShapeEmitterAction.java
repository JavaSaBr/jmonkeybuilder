package com.ss.editor.ui.control.model.tree.action.emitter.shape;

import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Dome;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The action to switch an emitter shape of the {@link ParticleEmitterNode} to a {@link Dome}.
 *
 * @author JavaSaBr
 */
public class CreateDomeShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    public CreateDomeShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_DOME_SHAPE;
    }

    @NotNull
    @Override
    protected Mesh createMesh() {
        return new Dome(10, 10, 1);
    }
}
