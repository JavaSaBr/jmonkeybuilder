package com.ss.editor.control.painting;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement a painting control.
 *
 * @author JavaSaBr
 */
public interface PaintingControl extends Control {

    /**
     * Get the painted model.
     *
     * @return the painted model.
     */
    @JmeThread
    default @Nullable Spatial getPaintedModel() {
        return null;
    }

    /**
     * Start painting.
     *
     * @param paintingInput the type of input.
     * @param contactPoint  the contact point.
     */
    @JmeThread
    default void startPainting(@NotNull final PaintingInput paintingInput, @NotNull final Vector3f contactPoint) {
    }

    /**
     * Get the current painting input.
     *
     * @return the current painting input.
     */
    @JmeThread
    default @Nullable PaintingInput getCurrentInput() {
        return null;
    }

    /**
     * Finish painting.
     *
     * @param contactPoint the contact point.
     */
    @JmeThread
    default void finishPainting(@NotNull final Vector3f contactPoint) {
    }

    /**
     * Update painting.
     *
     * @param contactPoint the contact point.
     */
    @JmeThread
    default void updatePainting(@NotNull final Vector3f contactPoint) {
    }

    /**
     * Return true of this control was started painting.
     *
     * @return true if this control started painting.
     */
    @JmeThread
    default boolean isStartedPainting() {
        return false;
    }
}
