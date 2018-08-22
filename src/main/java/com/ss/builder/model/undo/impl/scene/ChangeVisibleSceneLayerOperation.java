package com.ss.builder.model.undo.impl.scene;

import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AbstractEditorOperation;
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
    private volatile boolean needShow;

    public ChangeVisibleSceneLayerOperation(@NotNull SceneLayer layer, boolean needShow) {
        this.layer = layer;
        this.needShow = needShow;
    }

    @Override
    @JmeThread
    protected void startInJme(@NotNull ModelChangeConsumer editor) {

        super.startInJme(editor);

        var currentModel = editor.getCurrentModel();

        if (needShow && !layer.isShowed()) {
            layer.show();
            currentModel.depthFirstTraversal(this::updateSpatial);
        } else if (!needShow && layer.isShowed()) {
            layer.hide();
            currentModel.depthFirstTraversal(this::updateSpatial);
        }

        needShow = !needShow;
    }

    @Override
    @FxThread
    protected void endInFx(@NotNull ModelChangeConsumer editor) {
        super.endInFx(editor);
        editor.notifyFxChangeProperty(layer, "Showed");
    }

    @JmeThread
    private void updateSpatial(@NotNull Spatial spatial) {
        if (SceneLayer.getLayer(spatial) == layer) {
            spatial.setVisible(layer.isShowed());
        }
    }
}
