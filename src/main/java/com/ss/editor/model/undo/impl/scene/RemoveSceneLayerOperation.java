package com.ss.editor.model.undo.impl.scene;

import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.model.node.layer.LayersRoot;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;

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

    public RemoveSceneLayerOperation(
            @NotNull LayersRoot layersRoot,
            @NotNull SceneLayer layer,
            @NotNull SceneNode sceneNode
    ) {

        this.toRevert = Array.ofType(Spatial.class);
        this.layersRoot = layersRoot;
        this.layer = layer;
        this.sceneNode = sceneNode;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);

        var currentModel = editor.getCurrentModel();
        currentModel.depthFirstTraversal(this::clean);

        sceneNode.removeLayer(layer);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxRemovedChild(layersRoot, layer);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);

        sceneNode.addLayer(layer);

        toRevert.forEach(spatial -> SceneLayer.setLayer(layer, spatial));
        toRevert.forEach(spatial -> spatial.setVisible(layer.isShowed()));
        toRevert.clear();
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxAddedChild(layersRoot, layer, -1, false);
    }

    @JmeThread
    private void clean(@NotNull Spatial spatial) {
        var currentLayer = SceneLayer.getLayer(spatial);
        if (currentLayer == layer) {
            toRevert.add(spatial);
            SceneLayer.setLayer(null, spatial);
            spatial.setVisible(true);
        }
    }
}
