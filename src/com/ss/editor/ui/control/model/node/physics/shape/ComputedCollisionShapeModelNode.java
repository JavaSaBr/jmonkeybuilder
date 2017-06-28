package com.ss.editor.ui.control.model.node.physics.shape;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
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

import java.util.List;

/**
 * The implementation of node to show {@link CompoundCollisionShape}.
 *
 * @author JavaSaBr
 */
public class ComputedCollisionShapeModelNode extends CollisionShapeModelNode<CompoundCollisionShape> {

    /**
     * Instantiates a new Computed collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ComputedCollisionShapeModelNode(@NotNull final CompoundCollisionShape element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final CompoundCollisionShape element = getElement();
        final List<ChildCollisionShape> children = element.getChildren();
        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        children.forEach(childCollisionShape -> result.add(createFor(childCollisionShape)));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return true;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.ATOM_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_COMPUTED_COLLISION_SHAPE;
    }
}
