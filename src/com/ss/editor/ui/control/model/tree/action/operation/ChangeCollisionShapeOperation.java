package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to change a collision shape of a model.
 *
 * @author JavaSaBr
 */
public class ChangeCollisionShapeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new shape.
     */
    @NotNull
    private final CollisionShape newShape;

    /**
     * The previous shape.
     */
    @NotNull
    private final CollisionShape oldShape;

    /**
     * The collision object.
     */
    @NotNull
    private final PhysicsCollisionObject collisionObject;

    /**
     * Instantiates a new Change collision shape operation.
     *
     * @param newShape        the new shape
     * @param oldShape        the old shape
     * @param collisionObject the collision object
     */
    public ChangeCollisionShapeOperation(@NotNull final CollisionShape newShape, @NotNull final CollisionShape oldShape,
                                         @NotNull final PhysicsCollisionObject collisionObject) {
        this.newShape = newShape;
        this.oldShape = oldShape;
        this.collisionObject = collisionObject;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(collisionObject, oldShape));
            collisionObject.setCollisionShape(newShape);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(collisionObject, newShape, -1));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(collisionObject, newShape));
            collisionObject.setCollisionShape(oldShape);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(collisionObject, oldShape, -1));
        });
    }
}
