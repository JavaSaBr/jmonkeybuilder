package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} for removing a {@link Spatial} from the {@link Node}.
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

    /**
     * Instantiates a new Remove child operation.
     *
     * @param child  the child
     * @param parent the parent
     */
    public RemoveChildOperation(@NotNull final Spatial child, @NotNull final Node parent) {
        this.child = child;
        this.parent = parent;
        this.childIndex = parent.getChildIndex(child);
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.detachChild(child);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(parent, child));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.attachChildAt(child, childIndex);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(parent, child, childIndex));
        });
    }
}
