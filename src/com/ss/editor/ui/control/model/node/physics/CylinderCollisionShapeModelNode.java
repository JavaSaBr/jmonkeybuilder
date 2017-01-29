package com.ss.editor.ui.control.model.node.physics;

import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link CylinderCollisionShape}.
 *
 * @author JavaSaBr
 */
public class CylinderCollisionShapeModelNode extends CollisionShapeModelNode<CylinderCollisionShape> {

    public CylinderCollisionShapeModelNode(@NotNull final CylinderCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.CYLINDER_16;
    }

    @NotNull
    @Override
    public String getName() {
        return "Cylinder shape";
    }
}
