package com.ss.editor.ui.control.model.tree.action.emitter.mesh;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataMeshInfo;
import tonegod.emitter.particle.ParticleDataPointMesh;

/**
 * The action for switching the particle mesh of the {@link ParticleGeometry} to {@link
 * ParticleDataPointMesh}.
 *
 * @author JavaSaBr
 */
public class CreatePointParticleMeshAction extends AbstractCreateParticleMeshAction {

    public CreatePointParticleMeshAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_PARTICLES_MESH_POINT;
    }

    @NotNull
    @Override
    protected ParticleDataMeshInfo createMeshInfo() {
        return new ParticleDataMeshInfo(ParticleDataPointMesh.class, null);
    }
}
