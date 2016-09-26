package com.ss.editor.ui.control.model.tree.action.emitter;

import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Quad;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link Quad}.
 *
 * @author JavaSaBr
 */
public class CreateQuadShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    public CreateQuadShapeEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_QUAD_SHAPE;
    }

    @NotNull
    @Override
    protected Mesh createMesh() {
        return new Quad(1, 1, true);
    }
}
