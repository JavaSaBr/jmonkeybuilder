package com.ss.editor.ui.control.tree.action.impl.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove {@link Spatial} from the {@link Node}.
 *
 * @author JavaSaBr.
 */
public class RemoveChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The child to remove.
     */
    @NotNull
    private final Spatial child;

    /**
     * The parent element.
     */
    @NotNull
    private final Node parent;

    /**
     * The index of position in the parent.
     */
    private final int childIndex;

    public RemoveChildOperation(@NotNull final Spatial child, @NotNull final Node parent) {
        this.child = child;
        this.parent = parent;
        this.childIndex = parent.getChildIndex(child);
    }

    @Override
    @JmeThread
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            parent.detachChild(child);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxRemovedChild(parent, child));
        });
    }

    @Override
    @JmeThread
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            parent.attachChildAt(child, childIndex);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxAddedChild(parent, child, childIndex, false));
        });
    }
}
