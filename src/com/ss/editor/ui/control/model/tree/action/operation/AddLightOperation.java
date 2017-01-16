package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} for adding a {@link Light} to a {@link Node}.
 *
 * @author JavaSaBr
 */
public class AddLightOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new light.
     */
    private final Light light;

    /**
     * The index of parent position.
     */
    private final int index;

    public AddLightOperation(final Light light, final int index) {
        this.light = light;
        this.index = index;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.addLight(light);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedLight(node, light, -1));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof Node)) return;

            final Node node = (Node) parent;
            node.removeLight(light);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedLight(node, light));
        });
    }
}
