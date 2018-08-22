package com.ss.builder.fx.control.tree.node.impl.physics.shape;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} to show a {@link CollisionShape} in the tree.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class CollisionShapeTreeNode<T extends CollisionShape> extends TreeNode<T> {

    public CollisionShapeTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final T element = getElement();
        return element.getClass().getSimpleName();
    }
}
