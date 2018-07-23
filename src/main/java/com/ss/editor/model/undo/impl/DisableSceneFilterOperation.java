package com.ss.editor.model.undo.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
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
    private final SceneFilter sceneFilter;

    public DisableSceneFilterOperation(@NotNull final SceneFilter sceneFilter) {
        this.sceneFilter = sceneFilter;
    }

    @Override
    @FxThread
    protected void redoInFx(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            sceneFilter.setEnabled(false);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyChangedFilter(sceneFilter));
        });
    }

    @Override
    @FxThread
    protected void undoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            sceneFilter.setEnabled(true);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyChangedFilter(sceneFilter));
        });
    }
}
