package com.ss.editor.ui.control.model.node.control.physics;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.scene.control.Control;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.ControlModelNode;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PHYSICS_16;
    }
}
