package com.ss.editor.ui.control.model.node.physics;

import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link MeshCollisionShape}.
 *
 * @author JavaSaBr
 */
public class MeshCollisionShapeModelNode extends CollisionShapeModelNode<MeshCollisionShape> {

    public MeshCollisionShapeModelNode(@NotNull final MeshCollisionShape element, final long objectId) {
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
        return "Mesh shape";
    }
}
