package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a new {@link Spatial} to a {@link Node}.
 *
 * @author JavaSaBr.
 */
public class AddChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new child.
     */
    @NotNull
    private final Spatial newChild;

    /**
     * The parent.
     */
    @NotNull
    private final Node parent;

    /**
     * Instantiates a new Add child operation.
     *
     * @param newChild the new child
     * @param parent   the parent
     */
    public AddChildOperation(@NotNull final Spatial newChild, @NotNull final Node parent) {
        this.newChild = newChild;
        this.parent = parent;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.attachChildAt(newChild, 0);

            final TonegodTranslucentBucketFilter filter = EDITOR.getTranslucentBucketFilter();
            filter.refresh();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(parent, newChild, 0));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.detachChild(newChild);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(parent, newChild));
        });
    }
}
