package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataMeshInfo;
import tonegod.emitter.particle.ParticleDataTriMesh;

/**
 * The action for switching the particle mesh of the {@link ParticleGeometry} to {@link
 * ParticleDataTriMesh}.
 *
 * @author JavaSaBr
 */
public class CreateQuadParticleMeshAction extends AbstractCreateParticleMeshAction {

    public CreateQuadParticleMeshAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_QUAD;
    }

    @NotNull
    @Override
    protected ParticleDataMeshInfo createMeshInfo() {
        return new ParticleDataMeshInfo(ParticleDataTriMesh.class, null);
    }
}
