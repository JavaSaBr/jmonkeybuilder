package com.ss.editor.ui.control.model.tree.action.operation.scene;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove a layer from a scene.
 *
 * @author JavaSaBr
 */
public class RemoveSceneLayerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The list with spatials which uses this layer.
     */
    @NotNull
    private final Array<Spatial> toRevert;

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
        this.toRevert = ArrayFactory.newArray(Spatial.class);
        this.layersRoot = layersRoot;
        this.layer = layer;
        this.sceneNode = sceneNode;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final Spatial currentModel = editor.getCurrentModel();
            currentModel.depthFirstTraversal(this::clean);
            sceneNode.removeLayer(layer);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(layersRoot, layer));
        });
    }

    private void clean(@NotNull final Spatial spatial) {
        final SceneLayer currentLayer = SceneLayer.getLayer(spatial);
        if (currentLayer == layer) {
            toRevert.add(spatial);
            SceneLayer.setLayer(null, spatial);
            spatial.setVisible(true);
        }
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneNode.addLayer(layer);
            toRevert.forEach(spatial -> SceneLayer.setLayer(layer, spatial));
            toRevert.forEach(spatial -> spatial.setVisible(layer.isShowed()));
            toRevert.clear();
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(layersRoot, layer, -1));
        });
    }
}
