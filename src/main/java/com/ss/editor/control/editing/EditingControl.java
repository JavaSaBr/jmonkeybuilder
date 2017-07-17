package com.ss.editor.control.editing;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.JMEThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement editing control.
 *
 * @author JavaSaBr
 */
public interface EditingControl extends Control {

    /**
     * Gets edited model.
     *
     * @return the edited model.
     */
    @Nullable
    @JMEThread
    default Spatial getEditedModel() {
        return null;
    }

    /**
     * Start editing.
     *
     * @param editingInput the type of input.
     * @param contactPoint the contact point.
     */
    @JMEThread
    default void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
    }

    /**
     * Get a current editing input.
     *
     * @return the current editing input.
     */
    @Nullable
    default EditingInput getCurrentInput() {
        return null;
    }

    /**
     * Finish editing.
     *
     * @param contactPoint the contact point.
     */
    @JMEThread
    default void finishEditing(@NotNull final Vector3f contactPoint) {
    }

    /**
     * Update editing.
     *
     * @param contactPoint the contact point.
     */
    @JMEThread
    default void updateEditing(@NotNull final Vector3f contactPoint) {
    }

    /**
     * Is started editing boolean.
     *
     * @return true if this control started editing.
     */
    @JMEThread
    default boolean isStartedEditing() {
        return false;
    }
}
