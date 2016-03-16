package com.ss.editor.control.transform;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Интерфейс для реализации контролера редактора сцены.
 *
 * @author Ronn
 */
public interface SceneEditorControl {

    public enum TransformType {
        MoveTool,
        RotateTool,
        ScaleTool,
        None
    }

    public enum PickedAxis {
        X, Y, Z, XY, XZ, YZ, View, scaleAll, None
    }

    /**
     * @return центр трансформации.
     */
    public Transform getTransformCenter();

    /**
     * Установка направления трансформаци.
     */
    public void setPickedAxis(final PickedAxis axis);

    /**
     * @return текущее направление трансформации.
     */
    public PickedAxis getPickedAxis();

    /**
     * @return плоскость для вычисления трансформаций.
     */
    public Node getCollisionPlane();

    /**
     * Разница между предыдущей точкой трансформации и новой.
     */
    public void setDeltaVector(final Vector3f deltaVector);

    /**
     * @return разница между предыдущей точкой трансформации и новой.
     */
    public Vector3f getDeltaVector();

    /**
     * @return часть модели на трансформацию.
     */
    public Spatial getToTransform();

    /**
     * Уведомление об изменении трансформации указанной части модели.
     */
    public void notifyTransformed(final Spatial spatial);
}
