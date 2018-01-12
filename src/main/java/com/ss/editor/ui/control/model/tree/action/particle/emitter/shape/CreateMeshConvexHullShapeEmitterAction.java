package com.ss.editor.ui.control.model.tree.action.particle.emitter.shape;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterMeshConvexHullShape;
import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The action to create a {@link EmitterMeshConvexHullShape} to the {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class CreateMeshConvexHullShapeEmitterAction extends CreateMeshVertexShapeEmitterAction {

    public CreateMeshConvexHullShapeEmitterAction(@NotNull final NodeTree<?> nodeTree,
                                                  @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @FxThread
    @Override
    protected @NotNull EmitterMeshVertexShape createEmitterShape(@NotNull final List<Mesh> meshes) {
        return new EmitterMeshConvexHullShape(meshes);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_MESH_CONVEX_HULL_SHAPE;
    }
}
