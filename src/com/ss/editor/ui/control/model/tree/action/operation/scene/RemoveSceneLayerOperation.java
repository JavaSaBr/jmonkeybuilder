package com.ss.editor.ui.control.model.tree.action.operation.scene;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.ui.control.layer.LayersRoot;
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
     * The layer layersRoot.
     */
    @NotNull
    private final LayersRoot layersRoot;

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

    public RemoveSceneLayerOperation(final @NotNull LayersRoot layersRoot, @NotNull final SceneLayer layer,
                                     @NotNull final SceneNode sceneNode) {
        this.layersRoot = layersRoot;
        this.layer = layer;
        this.sceneNode = sceneNode;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneNode.removeLayer(layer);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(layersRoot, layer));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneNode.addLayer(layer);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(layersRoot, layer, -1));
        });
    }
}
