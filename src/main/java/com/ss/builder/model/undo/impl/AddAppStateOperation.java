package com.ss.builder.model.undo.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a new {@link SceneAppState} to a {@link SceneNode}.
 *
 * @author JavaSaBr.
 */
public class AddAppStateOperation extends AbstractEditorOperation<SceneChangeConsumer> {

    /**
     * The new scene app state.
     */
    @NotNull
    private final SceneAppState newState;

    /**
     * The scene node.
     */
    @NotNull
    private final SceneNode sceneNode;

    public AddAppStateOperation(@NotNull SceneAppState newState, @NotNull SceneNode sceneNode) {
        this.newState = newState;
        this.sceneNode = sceneNode;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull SceneChangeConsumer editor) {
        super.redoInJme(editor);
        sceneNode.addAppState(newState);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull SceneChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyAddedAppState(newState);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull SceneChangeConsumer editor) {
        super.undoInJme(editor);
        sceneNode.removeAppState(newState);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull SceneChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyRemovedAppState(newState);
    }
}
