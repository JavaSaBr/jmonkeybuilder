package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterMeshConvexHullShape;
import com.ss.editor.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link EmitterShapeTreeNode} for representing the {@link EmitterMeshConvexHullShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterMeshConvexHullShapeTreeNode extends EmitterShapeTreeNode {

    /**
     * Instantiates a new Emitter mesh convex hull shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public EmitterMeshConvexHullShapeTreeNode(@NotNull final EmitterMeshConvexHullShape element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_CONVEX_HULL;
    }
}
