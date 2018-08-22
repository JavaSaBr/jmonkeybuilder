package com.ss.builder.model.undo.impl;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AbstractEditorOperation;

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

    public RemoveControlOperation(@NotNull Control control, @NotNull Spatial parent) {
        this.control = control;
        this.parent = parent;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        parent.removeControl(control);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxRemovedChild(parent, control);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        parent.addControl(control);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxAddedChild(parent, control, -1, false);
    }
}
