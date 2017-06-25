package com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape;

import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link EmitterShapeModelNode} for representing the {@link EmitterMeshVertexShape} in the editor.
 *
 * @author JavaSaBr
 */
public class EmitterMeshVertexShapeModelNode extends EmitterShapeModelNode {

    /**
     * Instantiates a new Emitter mesh vertex shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public EmitterMeshVertexShapeModelNode(@NotNull final EmitterMeshVertexShape element, final long objectId) {
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
        return "Mesh vertex shape";
    }
}
