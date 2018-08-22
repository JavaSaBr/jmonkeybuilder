package com.ss.builder.model.undo.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
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

    public DisableAppStateOperation(@NotNull SceneAppState appState) {
        this.appState = appState;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull SceneChangeConsumer editor) {
        super.redoInJme(editor);
        appState.setEnabled(false);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull SceneChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyChangedAppState(appState);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull SceneChangeConsumer editor) {
        super.undoInJme(editor);
        appState.setEnabled(true);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull SceneChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyChangedAppState(appState);
    }
}
