package com.ss.editor.ui.control.model.tree.action.operation.scene;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove a layer from a scene.
 *
 * @author JavaSaBr
 */
public class RemoveSceneLayerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The removed layer.
     */
    @NotNull
    private final SceneLayer layer;

    /**
     * The scene node.
     */
    @NotNull
    private final SceneNode sceneNode;

    public RemoveSceneLayerOperation(@NotNull final SceneLayer layer, @NotNull final SceneNode sceneNode) {
        this.layer = layer;
        this.sceneNode = sceneNode;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneNode.removeLayer(layer);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(sceneNode, layer));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneNode.addLayer(layer);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(sceneNode, layer, -1));
        });
    }
}
