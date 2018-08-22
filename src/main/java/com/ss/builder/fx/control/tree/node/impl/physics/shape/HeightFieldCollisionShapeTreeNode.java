package com.ss.builder.ui.control.tree.node.impl.physics.shape;

import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link HeightfieldCollisionShape}.
 *
 * @author JavaSaBr
 */
public class HeightFieldCollisionShapeTreeNode extends CollisionShapeTreeNode<HeightfieldCollisionShape> {

    public HeightFieldCollisionShapeTreeNode(@NotNull final HeightfieldCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_HEIGHT_FIELD_COLLISION_SHAPE;
    }
}
