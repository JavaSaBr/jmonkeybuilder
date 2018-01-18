package com.ss.editor.ui.control.tree.node.impl.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link EmitterShapeTreeNode} for representing the {@link EmitterMeshVertexShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterMeshVertexShapeTreeNode extends EmitterShapeTreeNode {

    public EmitterMeshVertexShapeTreeNode(@NotNull final EmitterMeshVertexShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_SHAPE_MESH_VERTEX;
    }
}
