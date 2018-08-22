package com.ss.builder.ui.control.tree.node.impl.control.physics;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.PhysicsControl;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.control.tree.action.impl.physics.shape.*;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.impl.physics.shape.*;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} to show {@link PhysicsControl} in the tree.
 *
 * @param <T> the physics control's type.
 * @author JavaSaBr
 */
@SuppressWarnings("WeakerAccess")
public class PhysicsControlTreeNode<T extends PhysicsControl> extends ControlTreeNode<T> {


    public PhysicsControlTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final T element = getElement();
        final CollisionShape collisionShape = ((PhysicsCollisionObject) element).getCollisionShape();
        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class, 1);
        result.add(FACTORY_REGISTRY.createFor(collisionShape));

        return result;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return getElement() instanceof PhysicsCollisionObject;
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {

        if (getElement() instanceof PhysicsCollisionObject) {

            final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CHANGE_COLLISION_SHAPE, new ImageView(Icons.ADD_12));
            changeShapeMenu.getItems().addAll(new GenerateCollisionShapeAction(nodeTree, this),
                    new CreateBoxCollisionShapeAction(nodeTree, this),
                    new CreateCapsuleCollisionShapeAction(nodeTree, this),
                    new CreateConeCollisionShapeAction(nodeTree, this),
                    new CreateCylinderCollisionShapeAction(nodeTree, this),
                    new CreateSphereCollisionShapeAction(nodeTree, this));

            items.add(changeShapeMenu);
        }

        super.fillContextMenu(nodeTree, items);
    }


    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.PHYSICS_16;
    }
}
