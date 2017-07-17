package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The action to optimize geometry.
 *
 * @author JavaSaBr
 */
public class OptimizeGeometryOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new element.
     */
    @NotNull
    private final Spatial newSpatial;

    /**
     * The old element.
     */
    @NotNull
    private final Spatial oldSpatial;

    /**
     * The parent node.
     */
    @NotNull
    private final Node parent;

    /**
     * Instantiates a new Optimize geometry operation.
     *
     * @param newSpatial the new spatial
     * @param oldSpatial the old spatial
     * @param parent     the parent
     */
    public OptimizeGeometryOperation(@NotNull final Spatial newSpatial, @NotNull final Spatial oldSpatial,
                                     @NotNull final Node parent) {
        this.newSpatial = newSpatial;
        this.oldSpatial = oldSpatial;
        this.parent = parent;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final int index = parent.getChildIndex(oldSpatial);
            parent.detachChildAt(index);
            parent.attachChildAt(newSpatial, index);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyReplaced(parent, oldSpatial, newSpatial));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final int index = parent.getChildIndex(newSpatial);
            parent.detachChildAt(index);
            parent.attachChildAt(oldSpatial, index);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyReplaced(parent, newSpatial, oldSpatial));
        });
    }
}
