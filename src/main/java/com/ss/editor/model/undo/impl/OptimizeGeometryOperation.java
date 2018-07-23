package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
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

    public OptimizeGeometryOperation(@NotNull Spatial newSpatial, @NotNull Spatial oldSpatial, @NotNull Node parent) {
        this.newSpatial = newSpatial;
        this.oldSpatial = oldSpatial;
        this.parent = parent;
    }

    @Override
    @FxThread
    protected void redoInFx(@NotNull ModelChangeConsumer editor) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> apply(editor, oldSpatial, newSpatial));
    }

    @Override
    @FxThread
    protected void undoImpl(@NotNull ModelChangeConsumer editor) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> apply(editor, newSpatial, oldSpatial));
    }

    /**
     * Apply changes.
     *
     * @param consumer   the change consumer.
     * @param newSpatial the new spatial.
     * @param oldSpatial the new old spatial.
     */
    @JmeThread
    private void apply(
            @NotNull ModelChangeConsumer consumer, @NotNull Spatial newSpatial, @NotNull Spatial oldSpatial
    ) {

        var index = parent.getChildIndex(newSpatial);

        parent.detachChildAt(index);
        parent.attachChildAt(oldSpatial, index);

        ExecutorManager.getInstance()
                .addFxTask(() -> consumer.notifyFxReplaced(parent, newSpatial,
                        oldSpatial, true, false));
    }
}
