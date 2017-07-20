package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link TreeNode} to show a {@link ChildCollisionShape} in the tree.
 *
 * @author JavaSaBr
 */
public class ChildCollisionShapeTreeNode extends TreeNode<ChildCollisionShape> {

    /**
     * Instantiates a new Child collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ChildCollisionShapeTreeNode(@NotNull final ChildCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final ChildCollisionShape element = getElement();
        final CollisionShape shape = element.shape;

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class, 1);
        result.add(FACTORY_REGISTRY.createFor(shape));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_CHILD_COLLISION_SHAPE;
    }
}
