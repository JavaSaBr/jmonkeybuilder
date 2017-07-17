package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.ss.editor.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of node to show {@link BoxCollisionShape}.
 *
 * @author JavaSaBr
 */
public class BoxCollisionShapeModelNode extends CollisionShapeModelNode<BoxCollisionShape> {

    /**
     * Instantiates a new Box collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public BoxCollisionShapeModelNode(@NotNull final BoxCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_BOX_COLLISION_SHAPE;
    }
}
