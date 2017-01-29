package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link GImpactCollisionShape}.
 *
 * @author JavaSaBr
 */
public class GImpactCollisionShapeModelNode extends CollisionShapeModelNode<GImpactCollisionShape> {

    public GImpactCollisionShapeModelNode(@NotNull final GImpactCollisionShape element, final long objectId) {
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
        return "GImpact shape";
    }
}
