package com.ss.editor.model.undo.editor;

import com.ss.editor.annotation.FXThread;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.SceneAppState;

import org.jetbrains.annotations.NotNull;

/**
 * The interface to notify about any changes of a scene.
 *
 * @author JavaSaBr
 */
public interface SceneChangeConsumer extends ModelChangeConsumer {

    /**
     * Notify about added an app state.
     */
    @FXThread
    void notifyAddedAppState(@NotNull SceneAppState appState);

    /**
     * Notify about removed an app state.
     */
    @FXThread
    void notifyRemovedAppState(@NotNull SceneAppState appState);

    /**
     * Notify about changed an app state.
     */
    @FXThread
    void notifyChangedAppState(@NotNull SceneAppState appState);

    @NotNull
    @Override
    SceneNode getCurrentModel();
}
