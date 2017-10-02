package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.mesh;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataMeshInfo;
import tonegod.emitter.particle.ParticleDataPointMesh;

/**
 * The action to switch a particle mesh of the {@link ParticleGeometry} to {@link ParticleDataPointMesh}.
 *
 * @author JavaSaBr
 */
public class CreatePointParticleMeshAction extends AbstractCreateParticleMeshAction {

    /**
     * Instantiates a new Create point particle mesh action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreatePointParticleMeshAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @FXThread
    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.POINTS_16;
    }

    @FXThread
    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_PARTICLES_MESH_POINT;
    }

    @NotNull
    @Override
    protected ParticleDataMeshInfo createMeshInfo() {
        return new ParticleDataMeshInfo(ParticleDataPointMesh.class, null);
    }
}
