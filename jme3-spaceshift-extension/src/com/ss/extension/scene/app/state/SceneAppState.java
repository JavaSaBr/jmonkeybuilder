package com.ss.extension.scene.app.state;

import com.jme3.app.state.AppState;
import com.jme3.export.Savable;
import com.jme3.util.clone.JmeCloneable;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement a scene app state.
 *
 * @author JavaSaBr
 */
public interface SceneAppState extends AppState, Savable, Cloneable, JmeCloneable {

    /**
     * @return the name of this scene app state.
     */
    @NotNull
    String getName();

    /**
     * Set a scene node which is owner of this app state.
     *
     * @param sceneNode the scene node or null.
     */
    default void setSceneNode(@Nullable final SceneNode sceneNode) {
    }
}

