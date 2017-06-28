package com.ss.editor.ui.control.model.node.physics.shape;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelNode} to show a {@link CollisionShape} in the tree.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class CollisionShapeModelNode<T extends CollisionShape> extends ModelNode<T> {

    /**
     * Instantiates a new Collision shape model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public CollisionShapeModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @NotNull
    @Override
    public String getName() {
        final T element = getElement();
        return element.getClass().getSimpleName();
    }
}
