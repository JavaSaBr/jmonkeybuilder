package com.ss.editor.control.editing;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.EditorThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement editing control.
 *
 * @author JavaSaBr
 */
public interface EditingControl extends Control {

    /**
     * @return the edited model.
     */
    @Nullable
    @EditorThread
    default Spatial getEditedModel() {
        return null;
    }

    /**
     * Start editing.
     *
     * @param contactPoint the contact point.
     */
    @EditorThread
    default void startEditing(@NotNull final Vector3f contactPoint) {
    }

    /**
     * Finish editing.
     *
     * @param contactPoint the contact point.
     */
    @EditorThread
    default void finishEditing(@NotNull final Vector3f contactPoint) {
    }

    /**
     * Update editing.
     *
     * @param contactPoint the contact point.
     */
    @EditorThread
    default void updateEditing(@NotNull final Vector3f contactPoint) {
    }

    /**
     * @return true if this control started editing.
     */
    @EditorThread
    default boolean isStartedEditing() {
        return false;
    }
}
