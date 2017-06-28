package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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

    /**
     * Instantiates a new Remove light operation.
     *
     * @param light  the light
     * @param parent the parent
     */
    public RemoveLightOperation(@NotNull final Light light, @NotNull final Node parent) {
        this.light = light;
        this.parent = parent;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.removeLight(light);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(parent, light));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.addLight(light);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(parent, light, -1));
        });
    }
}
