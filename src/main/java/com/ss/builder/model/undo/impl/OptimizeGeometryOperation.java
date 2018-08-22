package com.ss.builder.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
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
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        apply(oldSpatial, newSpatial);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxReplaced(parent, oldSpatial, newSpatial, true, false);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        apply(newSpatial, oldSpatial);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxReplaced(parent, newSpatial, oldSpatial, true, false);
    }

    /**
     * Apply changes.
     *
     * @param newSpatial the new spatial.
     * @param oldSpatial the new old spatial.
     */
    @JmeThread
    private void apply(@NotNull Spatial newSpatial,@NotNull Spatial oldSpatial) {

        var index = parent.getChildIndex(newSpatial);

        parent.detachChildAt(index);
        parent.attachChildAt(oldSpatial, index);
    }
}
