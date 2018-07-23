package com.ss.editor.model.undo.impl;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.plugin.api.RenderFilterRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a new {@link Spatial} to a {@link Node}.
 *
 * @author JavaSaBr
 */
public class AddChildOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new child.
     */
    @NotNull
    private final Spatial newChild;

    /**
     * The parent.
     */
    @NotNull
    private final Node parent;

    /**
     * The flag to select added child.
     */
    private final boolean needSelect;

    public AddChildOperation(@NotNull Spatial newChild, @NotNull Node parent) {
        this(newChild, parent, true);
    }

    public AddChildOperation(@NotNull Spatial newChild, @NotNull Node parent, boolean needSelect) {
        this.newChild = newChild;
        this.parent = parent;
        this.needSelect = needSelect;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);

        editor.notifyJmePreChangeProperty(newChild, Messages.MODEL_PROPERTY_TRANSFORMATION);

        parent.attachChildAt(newChild, 0);

        editor.notifyJmeChangedProperty(newChild, Messages.MODEL_PROPERTY_TRANSFORMATION);

        RenderFilterRegistry.getInstance()
                .refreshFilters();
    }

    @Override
    @FxThread
    protected void finishRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishRedoInFx(editor);
        editor.notifyFxAddedChild(parent, newChild, 0, needSelect);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        parent.detachChild(newChild);
    }

    @Override
    @FxThread
    protected void finishUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishUndoInFx(editor);
        editor.notifyFxRemovedChild(parent, newChild);
    }
}
