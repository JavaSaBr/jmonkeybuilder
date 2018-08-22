package com.ss.builder.model.undo.impl;

import com.jme3.light.Light;
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
 * The implementation of the {@link AbstractEditorOperation} to remove a {@link Light} from the {@link Spatial}.
 *
 * @author JavaSaBr.
 */
public class RemoveLightOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The light to remove.
     */
    @NotNull
    private final Light light;

    /**
     * The parent.
     */
    @NotNull
    private final Node parent;

    public RemoveLightOperation(@NotNull Light light, @NotNull Node parent) {
        this.light = light;
        this.parent = parent;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        parent.removeLight(light);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxRemovedChild(parent, light);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        parent.addLight(light);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxAddedChild(parent, light, -1, false);
    }
}
