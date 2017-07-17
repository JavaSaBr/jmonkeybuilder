package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.ss.editor.Messages;
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

    /**
     * Instantiates a new Capsule collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
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
        return Messages.MODEL_FILE_EDITOR_NODE_CAPSULE_COLLISION_SHAPE;
    }
}
