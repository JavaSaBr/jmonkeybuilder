package com.ss.editor.ui.control.filter.operation;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.extension.scene.filter.SceneFilter;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to disable a {@link SceneFilter}.
 *
 * @author JavaSaBr.
 */
public class DisableSceneFilterOperation extends AbstractEditorOperation<SceneChangeConsumer> {

    /**
     * The scene filter.
     */
    @NotNull
    private final SceneFilter<?> sceneFilter;

    /**
     * Instantiates a new Disable scene filter operation.
     *
     * @param sceneFilter the scene filter
     */
    public DisableSceneFilterOperation(@NotNull final SceneFilter<?> sceneFilter) {
        this.sceneFilter = sceneFilter;
    }

    @Override
    protected void redoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneFilter.setEnabled(false);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangedFilter(sceneFilter));
        });
    }

    @Override
    protected void undoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneFilter.setEnabled(true);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangedFilter(sceneFilter));
        });
    }
}
