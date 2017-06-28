package com.ss.editor.ui.control.app.state.operation;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.SceneAppState;

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

    /**
     * Instantiates a new Add app state operation.
     *
     * @param newState  the new state
     * @param sceneNode the scene node
     */
    public AddAppStateOperation(@NotNull final SceneAppState newState, @NotNull final SceneNode sceneNode) {
        this.newState = newState;
        this.sceneNode = sceneNode;
    }

    @Override
    protected void redoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneNode.addAppState(newState);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedAppState(newState));
        });
    }

    @Override
    protected void undoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            sceneNode.removeAppState(newState);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedAppState(newState));
        });
    }
}
