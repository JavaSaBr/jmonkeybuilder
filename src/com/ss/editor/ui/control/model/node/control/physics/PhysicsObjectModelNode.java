package com.ss.editor.ui.control.model.node.control.physics;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.ControlModelNode;
import com.ss.editor.ui.control.model.tree.action.physics.shape.GenerateCollisionShapeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelNode} to show a {@link PhysicsCollisionObject} in the tree.
 *
 * @author JavaSaBr
 */
@SuppressWarnings("WeakerAccess")
public class PhysicsObjectModelNode<T extends PhysicsCollisionObject & Control> extends ControlModelNode<T> {

    public PhysicsObjectModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final T element = getElement();
        final CollisionShape collisionShape = element.getCollisionShape();

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class, 1);
        result.add(createFor(collisionShape));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return true;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {

        final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CHANGE_COLLISION_SHAPE, new ImageView(Icons.ADD_18));
        changeShapeMenu.getItems().addAll(new GenerateCollisionShapeAction(nodeTree, this));

        items.add(changeShapeMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PHYSICS_16;
    }
}
