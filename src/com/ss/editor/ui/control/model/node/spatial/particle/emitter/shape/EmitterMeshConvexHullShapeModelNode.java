package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterMeshConvexHullShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link EmitterShapeModelNode} for representing the {@link EmitterMeshConvexHullShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterMeshConvexHullShapeModelNode extends EmitterShapeModelNode {

    /**
     * Instantiates a new Emitter mesh convex hull shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public EmitterMeshConvexHullShapeModelNode(@NotNull final EmitterMeshConvexHullShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.MESH_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_CONVEX_HULL;
    }
}
