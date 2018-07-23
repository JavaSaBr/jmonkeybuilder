package com.ss.editor.model.undo.impl;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
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

    public ChangeCollisionShapeOperation(
            @NotNull CollisionShape newShape,
            @NotNull CollisionShape oldShape,
            @NotNull PhysicsCollisionObject collisionObject
    ) {
        this.newShape = newShape;
        this.oldShape = oldShape;
        this.collisionObject = collisionObject;
    }

    @Override
    @FxThread
    protected void startRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.startRedoInFx(editor);
        editor.notifyFxRemovedChild(collisionObject, oldShape);
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        collisionObject.setCollisionShape(newShape);
    }

    @Override
    @FxThread
    protected void finishRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishRedoInFx(editor);
        editor.notifyFxAddedChild(collisionObject, newShape, -1, true);
    }

    @Override
    @FxThread
    protected void startUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.startUndoInFx(editor);
        editor.notifyFxRemovedChild(collisionObject, newShape);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        collisionObject.setCollisionShape(oldShape);
    }

    @Override
    @FxThread
    protected void finishUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishUndoInFx(editor);
        editor.notifyFxAddedChild(collisionObject, oldShape, -1, false);
    }
}
