package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.ss.editor.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link EmitterShapeTreeNode} for representing the {@link EmitterMeshVertexShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterMeshVertexShapeTreeNode extends EmitterShapeTreeNode {

    /**
     * Instantiates a new Emitter mesh vertex shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public EmitterMeshVertexShapeTreeNode(@NotNull final EmitterMeshVertexShape element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_VERTEX;
    }
}
