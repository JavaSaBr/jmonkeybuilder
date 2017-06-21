package com.ss.editor.ui.control.model.tree.action.operation.scene;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.extension.scene.SceneLayer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a layer to a scene.
 *
 * @author JavaSaBr
 */
public class ChangeVisibleSceneLayerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The layer.
     */
    @NotNull
    private final SceneLayer layer;

    /**
     * The flag is need to show.
     */
    private boolean needShow;

    /**
     * Instantiates a new Change visible scene layer operation.
     *
     * @param layer    the layer
     * @param needShow the need show
     */
    public ChangeVisibleSceneLayerOperation(@NotNull final SceneLayer layer, final boolean needShow) {
        this.layer = layer;
        this.needShow = needShow;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();

            if (needShow && !layer.isShowed()) {
                layer.show();
                currentModel.depthFirstTraversal(this::updateSpatial);
            } else if (!needShow && layer.isShowed()) {
                layer.hide();
                currentModel.depthFirstTraversal(this::updateSpatial);
            }

            needShow = !needShow;

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, layer, "Showed"));
        });
    }

    private void updateSpatial(@NotNull final Spatial spatial) {
        if (SceneLayer.getLayer(spatial) != layer) return;
        spatial.setVisible(layer.isShowed());
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();

            if (needShow && !layer.isShowed()) {
                layer.show();
                currentModel.depthFirstTraversal(this::updateSpatial);
            } else if (!needShow && layer.isShowed()) {
                layer.hide();
                currentModel.depthFirstTraversal(this::updateSpatial);
            }

            needShow = !needShow;

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, layer, "Showed"));
        });
    }
}
