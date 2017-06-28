package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.mesh;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataImpostorMesh;
import tonegod.emitter.particle.ParticleDataMeshInfo;

/**
 * The action to switch a particle mesh of the {@link ParticleGeometry} to {@link ParticleDataImpostorMesh}.
 *
 * @author JavaSaBr
 */
public class CreateImpostorParticleMeshAction extends AbstractCreateParticleMeshAction {

    /**
     * Instantiates a new Create impostor particle mesh action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateImpostorParticleMeshAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.IMPOSTOR_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_IMPOSTOR;
    }

    @NotNull
    @Override
    protected ParticleDataMeshInfo createMeshInfo() {
        return new ParticleDataMeshInfo(ParticleDataImpostorMesh.class, null);
    }
}
