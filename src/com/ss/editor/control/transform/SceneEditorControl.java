package com.ss.editor.control.transform;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.jetbrains.annotations.Nullable;

/**
 * The interface for implementing a control of a scene editor.
 *
 * @author JavaSaBr
 */
public interface SceneEditorControl {

    enum TransformType {
        MOVE_TOOL,
        ROTATE_TOOL,
        SCALE_TOOL,
        NONE
    }

    enum PickedAxis {
        X, Y, Z, NONE
    }

    /**
     * @return the center of transformation.
     */
    @Nullable
    Transform getTransformCenter();

    /**
     * Установка направления трансформаци.
     */
    void setPickedAxis(final PickedAxis axis);

    /**
     * @return текущее направление трансформации.
     */
    PickedAxis getPickedAxis();

    /**
     * @return плоскость для вычисления трансформаций.
     */
    Node getCollisionPlane();

    /**
     * Разница между предыдущей точкой трансформации и новой.
     */
    void setDeltaVector(final Vector3f deltaVector);

    /**
     * @return разница между предыдущей точкой трансформации и новой.
     */
    Vector3f getDeltaVector();

    /**
     * @return часть модели на трансформацию.
     */
    Spatial getToTransform();

    /**
     * Уведомление об изменении трансформации указанной части модели.
     */
    void notifyTransformed(final Spatial spatial);
}
