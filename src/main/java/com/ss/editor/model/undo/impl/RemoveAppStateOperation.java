package com.ss.editor.model.undo.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove a {@link SceneAppState} from a {@link
 * SceneNode}*.
 *
 * @author JavaSaBr.
 */
public class RemoveAppStateOperation extends AbstractEditorOperation<SceneChangeConsumer> {

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

    public RemoveAppStateOperation(@NotNull SceneAppState newState, @NotNull SceneNode sceneNode) {
        this.newState = newState;
        this.sceneNode = sceneNode;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull SceneChangeConsumer editor) {
        super.redoInJme(editor);
        sceneNode.removeAppState(newState);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull SceneChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyRemovedAppState(newState);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull SceneChangeConsumer editor) {
        super.undoInJme(editor);
        sceneNode.addAppState(newState);
    }

    @Override
    @JmeThread
    protected void endUndoInFx(@NotNull SceneChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyAddedAppState(newState);
    }
}
