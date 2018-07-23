package com.ss.editor.model.undo.impl.scene;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.model.node.layer.LayersRoot;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a layer to a scene.
 *
 * @author JavaSaBr
 */
public class AddSceneLayerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The layers root.
     */
    @NotNull
    private final LayersRoot layersRoot;

    /**
     * The new layer.
     */
    @NotNull
    private final SceneLayer layer;

    /**
     * The scene node.
     */
    @NotNull
    private final SceneNode sceneNode;

    /**
     * Instantiates a new Add scene layer operation.
     *
     * @param layersRoot the layers root
     * @param layer      the layer
     * @param sceneNode  the scene node
     */
    public AddSceneLayerOperation(final @NotNull LayersRoot layersRoot, @NotNull final SceneLayer layer,
                                  @NotNull final SceneNode sceneNode) {
        this.layersRoot = layersRoot;
        this.layer = layer;
        this.sceneNode = sceneNode;
    }

    @Override
    protected void redoInFx(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            sceneNode.addLayer(layer);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxAddedChild(layersRoot, layer, -1, true));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            sceneNode.removeLayer(layer);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxRemovedChild(layersRoot, layer));
        });
    }
}
