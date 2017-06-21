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

    /**
     * The constant LOADED_MODEL_KEY.
     */
    String LOADED_MODEL_KEY = SceneEditorControl.class.getName() + ".loadedModel";
    /**
     * The constant SKY_NODE_KEY.
     */
    String SKY_NODE_KEY = SceneEditorControl.class.getName() + ".isSkyNode";

    /**
     * The enum Transform type.
     */
    enum TransformType {
        /**
         * Move tool transform type.
         */
        MOVE_TOOL,
        /**
         * Rotate tool transform type.
         */
        ROTATE_TOOL,
        /**
         * Scale tool transform type.
         */
        SCALE_TOOL,
        /**
         * None transform type.
         */
        NONE;

        private static final TransformType[] VALUES = values();

        /**
         * Value of transform type.
         *
         * @param index the index
         * @return the transform type
         */
        @NotNull
        public static TransformType valueOf(final int index) {
            return VALUES[index];
        }
    }

    /**
     * The enum Picked axis.
     */
    enum PickedAxis {
        /**
         * X picked axis.
         */
        X,
        /**
         * Y picked axis.
         */
        Y,
        /**
         * Z picked axis.
         */
        Z,
        /**
         * None picked axis.
         */
        NONE
    }

    /**
     * Gets transform center.
     *
     * @return the center of transformation.
     */
    @Nullable
    Transform getTransformCenter();

    /**
     * Sets picked axis.
     *
     * @param axis the picked axis.
     */
    void setPickedAxis(@NotNull final PickedAxis axis);

    /**
     * Gets picked axis.
     *
     * @return the picked axis.
     */
    @NotNull
    PickedAxis getPickedAxis();

    /**
     * Gets collision plane.
     *
     * @return the collision plane.
     */
    @Nullable
    Node getCollisionPlane();

    /**
     * Sets delta vector.
     *
     * @param deltaVector the delta vector.
     */
    void setDeltaVector(@Nullable final Vector3f deltaVector);

    /**
     * Gets delta vector.
     *
     * @return the delta vector.
     */
    @Nullable
    Vector3f getDeltaVector();

    /**
     * Gets to transform.
     *
     * @return the model to transform.
     */
    @Nullable
    Spatial getToTransform();

    /**
     * Notify transformed.
     *
     * @param spatial the model which was transformed.
     */
    void notifyTransformed(@NotNull final Spatial spatial);
}
