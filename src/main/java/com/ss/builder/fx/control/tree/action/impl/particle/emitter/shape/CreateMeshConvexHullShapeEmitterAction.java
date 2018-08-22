package com.ss.builder.fx.control.tree.action.impl.particle.emitter.shape;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterMeshConvexHullShape;
import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.jme3.scene.Mesh;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
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
