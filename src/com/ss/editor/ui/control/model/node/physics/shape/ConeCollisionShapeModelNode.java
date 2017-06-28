package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link ConeCollisionShape}.
 *
 * @author JavaSaBr
 */
public class ConeCollisionShapeModelNode extends CollisionShapeModelNode<ConeCollisionShape> {

    /**
     * Instantiates a new Cone collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ConeCollisionShapeModelNode(@NotNull final ConeCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.CONE_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_CONE_COLLISION_SHAPE;
    }
}
