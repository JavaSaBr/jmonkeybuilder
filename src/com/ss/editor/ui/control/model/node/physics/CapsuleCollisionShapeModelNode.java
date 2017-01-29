package com.ss.editor.ui.control.model.node.physics;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link CapsuleCollisionShape}.
 *
 * @author JavaSaBr
 */
public class CapsuleCollisionShapeModelNode extends CollisionShapeModelNode<CapsuleCollisionShape> {

    public CapsuleCollisionShapeModelNode(@NotNull final CapsuleCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.CAPSULE_16;
    }

    @NotNull
    @Override
    public String getName() {
        return "Capsule shape";
    }
}
