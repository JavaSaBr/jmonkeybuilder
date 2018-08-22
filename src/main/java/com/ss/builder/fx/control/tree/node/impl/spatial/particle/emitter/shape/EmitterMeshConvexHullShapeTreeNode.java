package com.ss.builder.ui.control.tree.node.impl.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterMeshConvexHullShape;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link EmitterShapeTreeNode} for representing the {@link EmitterMeshConvexHullShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterMeshConvexHullShapeTreeNode extends EmitterShapeTreeNode {

    public EmitterMeshConvexHullShapeTreeNode(@NotNull final EmitterMeshConvexHullShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_CONVEX_HULL;
    }
}
