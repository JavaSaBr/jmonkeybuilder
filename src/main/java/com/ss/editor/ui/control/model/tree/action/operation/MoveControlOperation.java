package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to move a control.
 *
 * @author JavaSaBr
 */
public class MoveControlOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The moved node.
     */
    @NotNull
    private final Control moved;

    /**
     * The old parent.
     */
    private Spatial oldParent;

    /**
     * The new parent.
     */
    private Spatial newParent;

    /**
     * Instantiates a new MoveControlOperation.
     *
     * @param moved     the moved
     * @param oldParent the old parent
     * @param newParent the new parent
     */
    public MoveControlOperation(@NotNull final Control moved, @NotNull final Spatial oldParent,
                                @NotNull final Spatial newParent) {
        this.moved = moved;
        this.oldParent = oldParent;
        this.newParent = newParent;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            oldParent.removeControl(moved);
            newParent.addControl(moved);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFXMoved(oldParent, newParent, moved, -1, true));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            newParent.removeControl(moved);
            oldParent.addControl(moved);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFXMoved(newParent, oldParent, moved, -1, false));
        });
    }
}
