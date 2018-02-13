package com.ss.editor.ui.control.tree.action.impl.operation;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

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

    public RemoveLightOperation(@NotNull final Light light, @NotNull final Node parent) {
        this.light = light;
        this.parent = parent;
    }

    @Override
    @JmeThread
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            parent.removeLight(light);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxRemovedChild(parent, light));
        });
    }

    @Override
    @JmeThread
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            parent.addLight(light);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxAddedChild(parent, light, -1, false));
        });
    }
}
