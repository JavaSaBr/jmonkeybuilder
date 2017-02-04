package com.ss.editor.control.transform;

import com.jme3.collision.CollisionResult;
import com.jme3.scene.control.Control;

import com.ss.editor.annotation.EditorThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a transformation control.
 *
 * @author JavaSaBr
 */
public interface TransformControl extends Control {

    /**
     * Set a collision result.
     */
    @EditorThread
    void setCollisionPlane(@NotNull final CollisionResult colResult);

    /**
     * Handle transformation.
     */
    @EditorThread
    void processTransform();
}
