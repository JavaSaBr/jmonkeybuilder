package com.ss.editor.model.undo.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
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

    public DisableSceneFilterOperation(@NotNull SceneFilter sceneFilter) {
        this.sceneFilter = sceneFilter;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull SceneChangeConsumer editor) {
        super.redoInJme(editor);
        sceneFilter.setEnabled(false);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull SceneChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyChangedFilter(sceneFilter);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull SceneChangeConsumer editor) {
        super.undoInJme(editor);
        sceneFilter.setEnabled(true);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull SceneChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyChangedFilter(sceneFilter);
    }

}
