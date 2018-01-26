package com.ss.editor.control.transform;

import static com.ss.editor.util.GeomUtils.*;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement supporting transformations in an editor.
 *
 * @author JavaSaBr
 */
public interface EditorTransformSupport {

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

        @NotNull
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
     * The enum Picked axis.
     */
    enum TransformationMode {
        /**
         * LOCAL MODE.
         */
        LOCAL {

        },
        /**
         * GLOBAL MODE.
         */
        GLOBAL {

            @Override
            @JmeThread
            public void prepareToRotate(@NotNull final Node parent, @NotNull final Node child,
                                        @NotNull final Transform transform, @NotNull final Camera camera) {
                parent.setLocalRotation(Quaternion.IDENTITY);
                child.setLocalRotation(transform.getRotation());
            }

            @NotNull
            @Override
            @JmeThread
            protected Vector3f getScaleAxis(@NotNull final Transform transform, @NotNull final PickedAxis pickedAxis,
                                               @NotNull final Camera camera) {

                final LocalObjects local = LocalObjects.get();

                if (pickedAxis == PickedAxis.Y) {
                    return getUp(transform.getRotation(), local.nextVector());
                } else if (pickedAxis == PickedAxis.Z) {
                    return getDirection(transform.getRotation(), local.nextVector());
                } else return getLeft(transform.getRotation(), local.nextVector());
            }

            @Override
            @JmeThread
            public void prepareToMove(@NotNull final Node parent, @NotNull final Node child,
                                      @NotNull final Transform transform, @NotNull final Camera camera) {
                parent.setLocalTranslation(Vector3f.ZERO);
                parent.setLocalRotation(Quaternion.IDENTITY);
                child.setLocalTranslation(transform.getTranslation());
                child.setLocalRotation(transform.getRotation());
            }

            @NotNull
            @Override
            @JmeThread
            public Quaternion getToolRotation(@NotNull final Transform transform, @NotNull final Camera camera) {
                return Quaternion.IDENTITY;
            }
        },
        /**
         * VIEW MODE,
         */
        VIEW {

            @Override
            @JmeThread
            public void prepareToRotate(@NotNull final Node parent, @NotNull final Node child,
                                        @NotNull final Transform transform, @NotNull final Camera camera) {
                parent.setLocalRotation(Quaternion.IDENTITY);
                child.setLocalRotation(transform.getRotation());
            }

            @NotNull
            @Override
            @JmeThread
            protected Vector3f getScaleAxis(@NotNull final Transform transform, @NotNull final PickedAxis pickedAxis,
                                               @NotNull final Camera camera) {

                final LocalObjects local = LocalObjects.get();

                if (pickedAxis == PickedAxis.Y) {
                    return getUp(camera.getRotation(), local.nextVector());
                } else if (pickedAxis == PickedAxis.Z) {
                    return getDirection(camera.getRotation(), local.nextVector());
                } else return getLeft(camera.getRotation(), local.nextVector());
            }

            @NotNull
            @Override
            @JmeThread
            protected Vector3f getPickedVector(@NotNull final Transform transform, @NotNull final PickedAxis pickedAxis,
                                               @NotNull final Camera camera) {

                final LocalObjects local = LocalObjects.get();

                if (pickedAxis == PickedAxis.Y) {
                    return getUp(camera.getRotation(), local.nextVector());
                } else if (pickedAxis == PickedAxis.Z) {
                    return getDirection(camera.getRotation(), local.nextVector());
                } else return getLeft(camera.getRotation(), local.nextVector());
            }

            @Override
            @JmeThread
            public void prepareToMove(@NotNull final Node parent, @NotNull final Node child,
                                      @NotNull final Transform transform, @NotNull final Camera camera) {
                parent.setLocalRotation(camera.getRotation());
                parent.setLocalTranslation(transform.getTranslation());
                child.setLocalTranslation(Vector3f.ZERO);
                child.setLocalRotation(Quaternion.IDENTITY);
            }

            @NotNull
            @Override
            @JmeThread
            public Quaternion getToolRotation(@NotNull final Transform transform, @NotNull final Camera camera) {
                return camera.getRotation();
            }
        };

        @NotNull
        private static final TransformationMode[] VALUES = values();

        @NotNull
        @FromAnyThread
        public static TransformationMode valueOf(final int index) {
            return VALUES[index];
        }

        /**
         * Gets the rotation of a transformation tool.
         *
         * @param transform the base transformation.
         * @param camera    the camera.
         * @return the tool rotation.
         */
        @NotNull
        @JmeThread
        public Quaternion getToolRotation(@NotNull final Transform transform, @NotNull final Camera camera) {
            return transform.getRotation();
        }

        /**
         * Prepare nodes to rotate.
         *
         * @param parent    the parent node.
         * @param child     the child node.
         * @param transform the base transform.
         * @param camera    the camera.
         */
        @JmeThread
        public void prepareToRotate(@NotNull final Node parent, @NotNull final Node child,
                                    @NotNull final Transform transform, @NotNull final Camera camera) {
            parent.setLocalRotation(transform.getRotation());
            child.setLocalRotation(Quaternion.IDENTITY);
        }

        /**
         * Prepare nodes to scale.
         *
         * @param parent    the parent node.
         * @param child     the child node.
         * @param transform the base transform.
         * @param camera    the camera.
         */
        @JmeThread
        public void prepareToScale(@NotNull final Node parent, @NotNull final Node child,
                                   @NotNull final Transform transform, @NotNull final Camera camera) {
            parent.setLocalScale(transform.getScale());
            child.setLocalScale(Vector3f.UNIT_XYZ);
        }

        /**
         * Prepare nodes to move.
         *
         * @param parent    the parent node.
         * @param child     the child node.
         * @param transform the base transform.
         * @param camera    the camera.
         */
        @JmeThread
        public void prepareToMove(@NotNull final Node parent, @NotNull final Node child,
                                  @NotNull final Transform transform, @NotNull final Camera camera) {
            parent.setLocalTranslation(transform.getTranslation());
            parent.setLocalRotation(transform.getRotation());
            child.setLocalTranslation(Vector3f.ZERO);
            child.setLocalRotation(Quaternion.IDENTITY);
        }

        /**
         * Get a vector to calculate transformations by the Axis.
         *
         * @param transform  the base transform.
         * @param pickedAxis the picked Axis.
         * @param camera     the camera.
         * @return the axis vector.
         */
        @NotNull
        @JmeThread
        protected Vector3f getPickedVector(@NotNull final Transform transform, @NotNull final PickedAxis pickedAxis,
                                           @NotNull final Camera camera) {

            if (pickedAxis == PickedAxis.Y) {
                return Vector3f.UNIT_Y;
            } else if (pickedAxis == PickedAxis.Z) {
                return Vector3f.UNIT_Z;
            } else return Vector3f.UNIT_X;
        }

        /**
         * Get a vector to calculate scaling by the axis.
         *
         * @param transform  the base transform.
         * @param pickedAxis the picked Axis.
         * @param camera     the camera.
         * @return the axis vector.
         */
        @NotNull
        @JmeThread
        protected Vector3f getScaleAxis(@NotNull final Transform transform, @NotNull final PickedAxis pickedAxis,
                                        @NotNull final Camera camera) {

            if (pickedAxis == PickedAxis.Y) {
                return Vector3f.UNIT_Y;
            } else if (pickedAxis == PickedAxis.Z) {
                return Vector3f.UNIT_Z;
            } else return Vector3f.UNIT_X;
        }
    }

    /**
     * Gets transform center.
     *
     * @return the center of transformation.
     */
    @Nullable
    @JmeThread
    Transform getTransformCenter();

    /**
     * Sets picked axis.
     *
     * @param axis the picked axis.
     */
    @JmeThread
    void setPickedAxis(@NotNull final PickedAxis axis);

    /**
     * Gets picked axis.
     *
     * @return the picked axis.
     */
    @NotNull
    @JmeThread
    PickedAxis getPickedAxis();

    /**
     * Gets the transform mode.
     *
     * @return the transform mode.
     */
    @NotNull
    @JmeThread
    EditorTransformSupport.TransformationMode getTransformationMode();

    /**
     * Gets collision plane.
     *
     * @return the collision plane.
     */
    @Nullable
    @JmeThread
    Node getCollisionPlane();

    /**
     * Set delta of transformation.
     *
     * @param transformDeltaX the x delta.
     */
    @JmeThread
    void setTransformDeltaX(float transformDeltaX);

    /**
     * Set delta of transformation.
     *
     * @param transformDeltaY the y delta.
     */
    @JmeThread
    void setTransformDeltaY(float transformDeltaY);

    /**
     * Set delta of transformation.
     *
     * @param transformDeltaZ the z delta.
     */
    @JmeThread
    void setTransformDeltaZ(float transformDeltaZ);

    /**
     * Get delta of transformation.
     *
     * @return the delta x.
     */
    @JmeThread
    float getTransformDeltaX();

    /**
     * Get delta of transformation.
     *
     * @return the delta y.
     */
    @JmeThread
    float getTransformDeltaY();

    /**
     * Get delta of transformation.
     *
     * @return the delta z.
     */
    @JmeThread
    float getTransformDeltaZ();

    /**
     * Gets to transform.
     *
     * @return the model to transform.
     */
    @Nullable
    @JmeThread
    Spatial getToTransform();

    /**
     * Notify transformed.
     *
     * @param spatial the model which was transformed.
     */
    @JmeThread
    void notifyTransformed(@NotNull final Spatial spatial);

    /**
     * Gets a camera.
     *
     * @return the camera.
     */
    @NotNull
    @JmeThread
    Camera getCamera();
}
