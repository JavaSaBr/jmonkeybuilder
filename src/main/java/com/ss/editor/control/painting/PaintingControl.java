package com.ss.editor.control.painting;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
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
    default @Nullable Object getPaintedModel() {
        return null;
    }

    /**
     * Set the brush size.
     *
     * @param brushSize the brush size.
     */
    @JmeThread
    void setBrushSize(float brushSize);

    /**
     * Set the brush power.
     *
     * @param brushPower the brush power.
     */
    @JmeThread
    void setBrushPower(float brushPower);

    /**
     * Start painting.
     *
     * @param brushRotation the brush rotation.
     * @param input         the type of input.
     * @param contactPoint  the contact point.
     */
    @JmeThread
    default void startPainting(@NotNull final PaintingInput input, @NotNull final Quaternion brushRotation,
                               @NotNull final Vector3f contactPoint) {
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
     * @param brushRotation the brush rotation.
     * @param contactPoint  the contact point.
     */
    @JmeThread
    default void finishPainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint) {
    }

    /**
     * Update painting.
     *
     * @param brushRotation the brush rotation.
     * @param contactPoint  the contact point.
     * @param tpf           the tpf.
     */
    @JmeThread
    default void updatePainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint,
                                float tpf) {
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
