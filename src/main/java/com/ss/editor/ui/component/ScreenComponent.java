package com.ss.editor.ui.component;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement a scene component.
 *
 * @author JavaSaBr
 */
public interface ScreenComponent {

    /**
     * Gets the component id.
     *
     * @return the component id.
     */
    @FromAnyThread
    default @Nullable String getComponentId() {
        return null;
    }

    /**
     * Notify about finishing building the result scene.
     */
    @FXThread
    default void notifyFinishBuild() {
    }
}
