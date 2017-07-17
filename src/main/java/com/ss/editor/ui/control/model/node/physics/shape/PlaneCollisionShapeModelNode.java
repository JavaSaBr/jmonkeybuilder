package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link PlaneCollisionShape}.
 *
 * @author JavaSaBr
 */
public class PlaneCollisionShapeModelNode extends CollisionShapeModelNode<PlaneCollisionShape> {

    /**
     * Instantiates a new Plane collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public PlaneCollisionShapeModelNode(@NotNull final PlaneCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PLANE_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PLANE_COLLISION_SHAPE;
    }
}
