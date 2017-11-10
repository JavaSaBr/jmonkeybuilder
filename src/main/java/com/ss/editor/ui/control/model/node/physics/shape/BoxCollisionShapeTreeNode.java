package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of node to show {@link BoxCollisionShape}.
 *
 * @author JavaSaBr
 */
public class BoxCollisionShapeTreeNode extends CollisionShapeTreeNode<BoxCollisionShape> {

    public BoxCollisionShapeTreeNode(@NotNull final BoxCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_BOX_COLLISION_SHAPE;
    }
}
