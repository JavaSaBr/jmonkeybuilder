package com.ss.editor.model.scene;

import com.jme3.util.SafeArrayList;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import org.jetbrains.annotations.NotNull;

/**
 * The scene app states node.
 *
 * @author JavaSaBr
 */
public class SceneAppStatesNode {

    /**
     * The scene node.
     */
    @NotNull
    private final SceneNode sceneNode;

    public SceneAppStatesNode(@NotNull SceneNode sceneNode) {
        this.sceneNode = sceneNode;
    }

    /**
     * Get the scene node.
     *
     * @return the scene node.
     */
    @FxThread
    public @NotNull SceneNode getSceneNode() {
        return sceneNode;
    }

    /**
     * Get the scene app states.
     *
     * @return the scene app states.
     */
    @FxThread
    public @NotNull SafeArrayList<SceneAppState> getAppStates() {
        return sceneNode.getAppStates();
    }
}
