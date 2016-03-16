package com.ss.editor.control.transform;

import com.jme3.collision.CollisionResult;
import com.jme3.scene.control.Control;

/**
 * Интерфейс для реализации контрола трансформации.
 *
 * @author Ronn
 */
public interface TransformControl extends Control {

    /**
     * Обработка колизии для активации трансформации.
     */
    public void setCollisionPlane(final CollisionResult colResult);

    /**
     * Процесс трансформации.
     */
    public void processTransform();
}
