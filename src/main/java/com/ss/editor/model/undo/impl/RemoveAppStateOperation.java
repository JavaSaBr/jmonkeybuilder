package com.ss.editor.model.undo.impl;

import com.ss.editor.annotation.FxThread;
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

    public RemoveAppStateOperation(@NotNull final SceneAppState newState, @NotNull final SceneNode sceneNode) {
        this.newState = newState;
        this.sceneNode = sceneNode;
    }

    @Override
    @FxThread
    protected void redoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            sceneNode.removeAppState(newState);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyRemovedAppState(newState));
        });
    }

    @Override
    @FxThread
    protected void undoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            sceneNode.addAppState(newState);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyAddedAppState(newState));
        });
    }
}
