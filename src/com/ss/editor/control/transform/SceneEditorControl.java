package com.ss.editor.control.transform;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement a control of a scene editor.
 *
 * @author JavaSaBr
 */
public interface SceneEditorControl {

    String LOADED_MODEL_KEY = SceneEditorControl.class.getName() + ".loadedModel";

    enum TransformType {
        MOVE_TOOL,
        ROTATE_TOOL,
        SCALE_TOOL,
        NONE;

        private static final TransformType[] VALUES = values();

        @NotNull
        public static TransformType valueOf(final int index) {
            return VALUES[index];
        }
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
     * @param axis the picked axis.
     */
    void setPickedAxis(@NotNull final PickedAxis axis);

    /**
     * @return the picked axis.
     */
    @NotNull
    PickedAxis getPickedAxis();

    /**
     * @return the collision plane.
     */
    @Nullable
    Node getCollisionPlane();

    /**
     * @param deltaVector the delta vector.
     */
    void setDeltaVector(@Nullable final Vector3f deltaVector);

    /**
     * @return the delta vector.
     */
    @Nullable
    Vector3f getDeltaVector();

    /**
     * @return the model to transform.
     */
    @Nullable
    Spatial getToTransform();

    /**
     * @param spatial the model which was transformed.
     */
    void notifyTransformed(@NotNull final Spatial spatial);
}
