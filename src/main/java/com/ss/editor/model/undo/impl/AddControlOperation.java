package com.ss.editor.model.undo.impl;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a control to a node.
 *
 * @author JavaSaBr
 */
public class AddControlOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new control.
     */
    @NotNull
    private final Control newControl;

    /**
     * The parent.
     */
    @NotNull
    private final Spatial spatial;

    public AddControlOperation(@NotNull Control newControl, @NotNull Spatial spatial) {
        this.newControl = newControl;
        this.spatial = spatial;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        spatial.addControl(newControl);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxAddedChild(spatial, newControl, -1, true);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        spatial.removeControl(newControl);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxRemovedChild(spatial, newControl);
    }
}
