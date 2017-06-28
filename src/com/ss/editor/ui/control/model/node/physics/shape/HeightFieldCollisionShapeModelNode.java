package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link HeightfieldCollisionShape}.
 *
 * @author JavaSaBr
 */
public class HeightFieldCollisionShapeModelNode extends CollisionShapeModelNode<HeightfieldCollisionShape> {

    /**
     * Instantiates a new Height field collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public HeightFieldCollisionShapeModelNode(@NotNull final HeightfieldCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_HEIGHT_FIELD_COLLISION_SHAPE;
    }
}
