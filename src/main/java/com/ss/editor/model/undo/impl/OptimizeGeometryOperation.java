package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
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

    public OptimizeGeometryOperation(@NotNull final Spatial newSpatial, @NotNull final Spatial oldSpatial,
                                     @NotNull final Node parent) {
        this.newSpatial = newSpatial;
        this.oldSpatial = oldSpatial;
        this.parent = parent;
    }

    @Override
    @FxThread
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> apply(editor, oldSpatial, newSpatial));
    }

    @Override
    @FxThread
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> apply(editor, newSpatial, oldSpatial));
    }

    /**
     * Apply changes.
     *
     * @param consumer   the change consumer.
     * @param newSpatial the new spatial.
     * @param oldSpatial the new old spatial.
     */
    @JmeThread
    private void apply(@NotNull final ModelChangeConsumer consumer, @NotNull final Spatial newSpatial,
                       @NotNull final Spatial oldSpatial) {

        final int index = parent.getChildIndex(newSpatial);
        parent.detachChildAt(index);
        parent.attachChildAt(oldSpatial, index);

        EXECUTOR_MANAGER.addFxTask(() -> consumer.notifyFxReplaced(parent, newSpatial, oldSpatial, true, false));
    }
}
