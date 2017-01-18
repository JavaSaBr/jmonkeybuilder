package com.ss.extension.scene.app.state;

import com.jme3.app.state.AppState;
import com.jme3.export.Savable;
import com.jme3.util.clone.JmeCloneable;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

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
     * Init this state to a scene node.
     *
     * @param sceneNode the scene node.
     */
    void initFor(@NotNull final SceneNode sceneNode);

    /**
     * cleanup this state from a scene node.
     *
     * @param sceneNode the scene node.
     */
    void cleanupFor(@NotNull final SceneNode sceneNode);
}

