package com.ss.editor.model.undo.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to disable a {@link SceneAppState}.
 *
 * @author JavaSaBr
 */
public class DisableAppStateOperation extends AbstractEditorOperation<SceneChangeConsumer> {

    /**
     * The scene app state.
     */
    @NotNull
    private final SceneAppState appState;

    public DisableAppStateOperation(@NotNull final SceneAppState appState) {
        this.appState = appState;
    }

    @Override
    @FxThread
    protected void redoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            appState.setEnabled(false);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyChangedAppState(appState));
        });
    }

    @Override
    @FxThread
    protected void undoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            appState.setEnabled(true);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyChangedAppState(appState));
        });
    }
}
