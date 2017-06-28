package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The operation to move a node.
 *
 * @author JavaSaBr
 */
public class MoveChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The moved node.
     */
    @NotNull
    private final Spatial moved;

    /**
     * The child index.
     */
    private final int childIndex;

    /**
     * The old parent.
     */
    private Node oldParent;

    /**
     * The new parent.
     */
    private Node newParent;

    /**
     * Instantiates a new Move child operation.
     *
     * @param moved      the moved
     * @param oldParent  the old parent
     * @param newParent  the new parent
     * @param childIndex the child index
     */
    public MoveChildOperation(@NotNull final Spatial moved, @NotNull final Node oldParent,
                              @NotNull final Node newParent, final int childIndex) {
        this.moved = moved;
        this.oldParent = oldParent;
        this.newParent = newParent;
        this.childIndex = childIndex;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            newParent.attachChildAt(moved, 0);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyMoved(oldParent, newParent, moved, 0));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            oldParent.attachChildAt(moved, childIndex);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyMoved(newParent, oldParent, moved, childIndex));
        });
    }
}
