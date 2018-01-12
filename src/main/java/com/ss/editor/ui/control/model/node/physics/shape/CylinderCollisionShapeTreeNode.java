package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link CylinderCollisionShape}.
 *
 * @author JavaSaBr
 */
public class CylinderCollisionShapeTreeNode extends CollisionShapeTreeNode<CylinderCollisionShape> {

    public CylinderCollisionShapeTreeNode(@NotNull final CylinderCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.CYLINDER_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_CYLINDER_COLLISION_SHAPE;
    }
}
