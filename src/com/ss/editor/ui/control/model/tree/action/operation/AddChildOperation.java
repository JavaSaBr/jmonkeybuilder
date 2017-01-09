package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

/**
 * The implementation of the {@link AbstractEditorOperation} for adding a new {@link Spatial} to a {@link Node}.
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
     * The index of parent position.
     */
    private final int index;

    public AddChildOperation(@NotNull final Spatial newChild, final int index) {
        this.newChild = newChild;
        this.index = index;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.attachChildAt(newChild, 0);

            final TonegodTranslucentBucketFilter filter = EDITOR.getTranslucentBucketFilter();
            filter.refresh();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(node, newChild, 0));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.detachChild(newChild);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(node, newChild));
        });
    }
}
