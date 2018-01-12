package com.ss.editor.control.transform;

import com.jme3.collision.CollisionResult;
import com.jme3.scene.control.Control;

import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a transformation control.
 *
 * @author JavaSaBr
 */
public interface TransformControl extends Control {

    /**
     * Set a collision result.
     *
     * @param colResult the col result
     */
    @JmeThread
    void setCollisionPlane(@NotNull final CollisionResult colResult);

    /**
     * Handle transformation.
     */
    @JmeThread
    void processTransform();
}
