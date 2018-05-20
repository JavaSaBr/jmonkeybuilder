package com.ss.editor.model.undo.impl;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove a control from a node.
 *
 * @author JavaSaBr
 */
public class RemoveControlOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The control.
     */
    @NotNull
    private final Control control;

    /**
     * The parent.
     */
    @NotNull
    private final Spatial parent;

    public RemoveControlOperation(@NotNull final Control control, @NotNull final Spatial parent) {
        this.control = control;
        this.parent = parent;
    }

    @Override
    @JmeThread
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            parent.removeControl(control);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxRemovedChild(parent, control));
        });
    }

    @Override
    @JmeThread
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            parent.addControl(control);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxAddedChild(parent, control, -1, false));
        });
    }
}
