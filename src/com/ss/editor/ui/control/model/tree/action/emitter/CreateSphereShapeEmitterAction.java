package com.ss.editor.ui.control.model.tree.action.emitter;

import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link Sphere}.
 *
 * @author JavaSaBr
 */
public class CreateSphereShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    public CreateSphereShapeEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_SPHERE_SHAPE;
    }

    @NotNull
    @Override
    protected Mesh createMesh() {
        return new Sphere(10, 10, 1);
    }
}
