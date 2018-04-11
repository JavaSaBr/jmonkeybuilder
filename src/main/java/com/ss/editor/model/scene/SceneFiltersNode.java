package com.ss.editor.model.scene;

import com.jme3.util.SafeArrayList;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.filter.SceneFilter;
import org.jetbrains.annotations.NotNull;

/**
 * The scene filters node.
 *
 * @author JavaSaBr
 */
public class SceneFiltersNode {

    /**
     * The scene node.
     */
    @NotNull
    private final SceneNode sceneNode;

    public SceneFiltersNode(@NotNull final SceneNode sceneNode) {
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
     * Get the scene filters.
     *
     * @return the scene filters.
     */
    @FxThread
    public @NotNull SafeArrayList<SceneFilter> getFilters() {
        return sceneNode.getFilters();
    }
}
