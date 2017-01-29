package com.ss.editor.ui.control.model.node.physics;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link SphereCollisionShape}.
 *
 * @author JavaSaBr
 */
public class SphereCollisionShapeModelNode extends CollisionShapeModelNode<SphereCollisionShape> {

    public SphereCollisionShapeModelNode(@NotNull final SphereCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.SPHERE_16;
    }

    @NotNull
    @Override
    public String getName() {
        return "Sphere shape";
    }
}
