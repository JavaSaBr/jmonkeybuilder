package com.ss.editor.ui.control.tree.node.impl.control.physics;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import com.ss.editor.ui.control.tree.action.impl.physics.shape.*;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link TreeNode} to show a {@link PhysicsCollisionObject} in the tree.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
@SuppressWarnings("WeakerAccess")
public class PhysicsObjectTreeNode<T extends PhysicsCollisionObject & Control> extends ControlTreeNode<T> {

    /**
     * Instantiates a new Physics object model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public PhysicsObjectTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final T element = getElement();
        final CollisionShape collisionShape = element.getCollisionShape();

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class, 1);
        result.add(FACTORY_REGISTRY.createFor(collisionShape));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }

    @Override
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {

        final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CHANGE_COLLISION_SHAPE, new ImageView(Icons.ADD_12));
        changeShapeMenu.getItems().addAll(new GenerateCollisionShapeAction(nodeTree, this),
                new CreateBoxCollisionShapeAction(nodeTree, this),
                new CreateCapsuleCollisionShapeAction(nodeTree, this),
                new CreateConeCollisionShapeAction(nodeTree, this),
                new CreateCylinderCollisionShapeAction(nodeTree, this),
                new CreateSphereCollisionShapeAction(nodeTree, this));

        items.add(changeShapeMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PHYSICS_16;
    }
}
