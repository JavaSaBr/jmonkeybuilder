package com.ss.editor.ui.control.model.node.physics.shape;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelNode} to show a {@link ChildCollisionShape} in the tree.
 *
 * @author JavaSaBr
 */
public class ChildCollisionShapeModelNode extends ModelNode<ChildCollisionShape> {

    /**
     * Instantiates a new Child collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ChildCollisionShapeModelNode(@NotNull final ChildCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final ChildCollisionShape element = getElement();
        final CollisionShape shape = element.shape;

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class, 1);
        result.add(createFor(shape));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
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
