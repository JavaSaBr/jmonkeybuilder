package com.ss.editor.ui.control.model.tree.action.operation.scene;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.extension.scene.SceneLayer;

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

    public ChangeVisibleSceneLayerOperation(@NotNull final SceneLayer layer, final boolean needShow) {
        this.layer = layer;
        this.needShow = needShow;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            if (needShow && !layer.isShowed()) {
                layer.show();
            } else if (!needShow && layer.isShowed()) {
                layer.hide();
            }

            needShow = !needShow;

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(layer.getSceneNode(), layer, "Showed"));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            if (needShow && !layer.isShowed()) {
                layer.show();
            } else if (!needShow && layer.isShowed()) {
                layer.hide();
            }

            needShow = !needShow;

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(layer.getSceneNode(), layer, "Showed"));
        });
    }
}
