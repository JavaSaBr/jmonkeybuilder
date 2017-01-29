package com.ss.editor.ui.control.model.node.physics;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of node to show {@link BoxCollisionShape}.
 *
 * @author JavaSaBr
 */
public class BoxCollisionShapeModelNode extends CollisionShapeModelNode<BoxCollisionShape> {

    public BoxCollisionShapeModelNode(@NotNull final BoxCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return "Box shape";
    }
}
