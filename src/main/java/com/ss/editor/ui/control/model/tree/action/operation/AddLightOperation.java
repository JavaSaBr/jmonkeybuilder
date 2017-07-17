package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a {@link Light} to a {@link Node}.
 *
 * @author JavaSaBr
 */
public class AddLightOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new light.
     */
    @NotNull
    private final Light light;

    /**
     * The parent.
     */
    @NotNull
    private final Node parent;

    /**
     * Instantiates a new Add light operation.
     *
     * @param light  the light
     * @param parent the parent
     */
    public AddLightOperation(@NotNull final Light light, @NotNull Node parent) {
        this.light = light;
        this.parent = parent;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.addLight(light);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(parent, light, -1));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            parent.removeLight(light);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(parent, light));
        });
    }
}
