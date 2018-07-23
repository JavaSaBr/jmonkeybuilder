package com.ss.editor.model.undo.impl;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to move a control.
 *
 * @author JavaSaBr
 */
public class MoveControlOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The moved node.
     */
    @NotNull
    private final Control moved;

    /**
     * The old parent.
     */
    @NotNull
    private Spatial oldParent;

    /**
     * The new parent.
     */
    @NotNull
    private Spatial newParent;

    public MoveControlOperation(
            @NotNull Control moved,
            @NotNull Spatial oldParent,
            @NotNull Spatial newParent
    ) {
        this.moved = moved;
        this.oldParent = oldParent;
        this.newParent = newParent;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        oldParent.removeControl(moved);
        newParent.addControl(moved);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxMoved(oldParent, newParent, moved, -1, true);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        newParent.removeControl(moved);
        oldParent.addControl(moved);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxMoved(newParent, oldParent, moved, -1, false);
    }
}
