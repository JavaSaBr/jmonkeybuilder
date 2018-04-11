package com.ss.editor.ui.control.tree.node.impl.physics.shape;

import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

import java.util.List;

/**
 * The implementation of node to show {@link CompoundCollisionShape}.
 *
 * @author JavaSaBr
 */
public class ComputedCollisionShapeTreeNode extends CollisionShapeTreeNode<CompoundCollisionShape> {

    public ComputedCollisionShapeTreeNode(@NotNull final CompoundCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final CompoundCollisionShape element = getElement();
        final List<ChildCollisionShape> children = element.getChildren();
        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        children.forEach(childCollisionShape -> result.add(FACTORY_REGISTRY.createFor(childCollisionShape)));

        return result;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.ATOM_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_COMPUTED_COLLISION_SHAPE;
    }
}
