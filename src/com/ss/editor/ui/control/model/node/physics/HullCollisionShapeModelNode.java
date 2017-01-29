package com.ss.editor.ui.control.model.node.physics;

import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link HullCollisionShape}.
 *
 * @author JavaSaBr
 */
public class HullCollisionShapeModelNode extends CollisionShapeModelNode<HullCollisionShape> {

    public HullCollisionShapeModelNode(@NotNull final HullCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.MESH_16;
    }

    @NotNull
    @Override
    public String getName() {
        return "Hull shape";
    }
}
