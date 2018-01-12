package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link GImpactCollisionShape}.
 *
 * @author JavaSaBr
 */
public class GImpactCollisionShapeTreeNode extends CollisionShapeTreeNode<GImpactCollisionShape> {

    public GImpactCollisionShapeTreeNode(@NotNull final GImpactCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.MESH_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_GIMPACT_COLLISION_SHAPE;
    }
}
