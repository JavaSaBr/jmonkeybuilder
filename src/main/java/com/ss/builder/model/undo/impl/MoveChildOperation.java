package com.ss.builder.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The operation to move a node.
 *
 * @author JavaSaBr
 */
public class MoveChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The moved node.
     */
    @NotNull
    private final Spatial moved;

    /**
     * The old parent.
     */
    @NotNull
    private final Node oldParent;

    /**
     * The new parent.
     */
    @NotNull
    private final Node newParent;

    /**
     * The child index.
     */
    private final int childIndex;

    public MoveChildOperation(
            @NotNull Spatial moved,
            @NotNull Node oldParent,
            @NotNull Node newParent,
            int childIndex
    ) {
        this.moved = moved;
        this.oldParent = oldParent;
        this.newParent = newParent;
        this.childIndex = childIndex;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        newParent.attachChildAt(moved, 0);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxMoved(oldParent, newParent, moved, 0, true);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        oldParent.attachChildAt(moved, childIndex);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxMoved(newParent, oldParent, moved, childIndex, false);
    }
}
