package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterMeshFaceShape;
import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The action to create a {@link EmitterMeshFaceShape} to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateMeshFaceShapeEmitterAction extends CreateMeshVertexShapeEmitterAction {

    /**
     * Instantiates a new Create mesh face shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateMeshFaceShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                            @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected EmitterMeshVertexShape createEmitterShape(@NotNull final List<Mesh> meshes) {
        return new EmitterMeshFaceShape(meshes);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_FACE_SHAPE;
    }
}
