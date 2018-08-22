package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove {@link Spatial} from the {@link Node}.
 *
 * @author JavaSaBr.
 */
public class RemoveChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The child to remove.
     */
    @NotNull
    private final Spatial child;

    /**
     * The parent element.
     */
    @NotNull
    private final Node parent;

    /**
     * The index of position in the parent.
     */
    private final int childIndex;

    public RemoveChildOperation(@NotNull Spatial child, @NotNull Node parent) {
        this.child = child;
        this.parent = parent;
        this.childIndex = parent.getChildIndex(child);
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        parent.detachChild(child);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxRemovedChild(parent, child);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        parent.attachChildAt(child, childIndex);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxAddedChild(parent, child, childIndex, false);
    }
}
