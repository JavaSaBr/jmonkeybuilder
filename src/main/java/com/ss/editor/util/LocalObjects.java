package com.ss.editor.util;

import static java.lang.Thread.currentThread;
import com.jme3.collision.CollisionResults;
import com.jme3.math.*;
import com.jme3.scene.Spatial;
import com.ss.editor.EditorThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.util.CycleBuffer;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.pools.Reusable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The container with local objects.
 *
 * @author JavaSaBr.
 */
public class LocalObjects {

    private static final int SIZE = 50;

    /**
     * Get the local objects.
     *
     * @return the local objects
     */
    public static @NotNull LocalObjects get() {
        return ((EditorThread) currentThread()).getLocal();
    }

    /**
     * The buffer of vectors.
     */
    @NotNull
    private final CycleBuffer<Vector3f> vectorBuffer;

    /**
     * The buffer of vectors.
     */
    @NotNull
    private final CycleBuffer<Vector2f> vector2fBuffer;

    /**
     * The buffer of planes.
     */
    @NotNull
    private final CycleBuffer<Plane> planeBuffer;

    /**
     * The buffer of colors.
     */
    @NotNull
    private final CycleBuffer<ColorRGBA> colorBuffer;

    /**
     * The buffer of rotation.
     */
    @NotNull
    private final CycleBuffer<Quaternion> rotationBuffer;

    /**
     * The buffer of rays.
     */
    @NotNull
    private final CycleBuffer<Ray> rayBuffer;

    /**
     * The buffer of collision results.
     */
    @NotNull
    private final CycleBuffer<ReusableCollisionResults> collisionResultsBuffer;

    /**
     * The buffer of matrix.
     */
    @NotNull
    private final CycleBuffer<Matrix3f> matrix3fBuffer;

    /**
     * The buffer of matrix float arrays.
     */
    @NotNull
    private final CycleBuffer<float[]> matrixFloatBuffer;

    /**
     * The buffer of object arrays.
     */
    @NotNull
    private final CycleBuffer<Array<Object>> objectArrayBuffer;

    /**
     * The buffer of spatial's arrays.
     */
    @NotNull
    private final CycleBuffer<Array<Spatial>> spatialArrayBuffer;

    @SuppressWarnings("unchecked")
    public LocalObjects() {
        this.vectorBuffer = new CycleBuffer<>(Vector3f.class, SIZE, Vector3f::new);
        this.vector2fBuffer = new CycleBuffer<>(Vector2f.class, SIZE, Vector2f::new);
        this.planeBuffer = new CycleBuffer<>(Plane.class, SIZE, Plane::new);
        this.rotationBuffer = new CycleBuffer<>(Quaternion.class, SIZE, Quaternion::new);
        this.rayBuffer = new CycleBuffer<>(Ray.class, SIZE, Ray::new);
        this.matrix3fBuffer = new CycleBuffer<>(Matrix3f.class, SIZE, Matrix3f::new);
        this.matrixFloatBuffer = new CycleBuffer<>(float[].class, SIZE, () -> new float[16]);
        this.colorBuffer = new CycleBuffer<>(ColorRGBA.class, SIZE, ColorRGBA::new);
        this.objectArrayBuffer = new CycleBuffer<>(Array.class, SIZE, () -> ArrayFactory.newArray(Object.class), Collection::clear);
        this.spatialArrayBuffer = new CycleBuffer<>(Array.class, SIZE, () -> ArrayFactory.newArray(Spatial.class), Collection::clear);
        this.collisionResultsBuffer = new CycleBuffer<>(ReusableCollisionResults.class, SIZE,
                ReusableCollisionResults::new, Reusable::free);
    }

    /**
     * Get the next free objects array.
     *
     * @return the next free objects array.
     */
    @FromAnyThread
    public @NotNull Array<Object> nextObjectArray() {
        return objectArrayBuffer.next();
    }

    /**
     * Get the next free spatial's array.
     *
     * @return the next free spatial's array.
     */
    @FromAnyThread
    public @NotNull Array<Spatial> nextSpatialArray() {
        return spatialArrayBuffer.next();
    }

    /**
     * Get next free matrix.
     *
     * @return the next free matrix.
     */
    @FromAnyThread
    public @NotNull Matrix3f nextMatrix3f() {
        return matrix3fBuffer.next();
    }

    /**
     * Get the next free matrix float array.
     *
     * @return the next free matrix float array.
     */
    @FromAnyThread
    public @NotNull float[] nextMatrixFloat() {
        return matrixFloatBuffer.next();
    }

    /**
     * Get the next free ray.
     *
     * @return the next free ray.
     */
    @FromAnyThread
    public @NotNull Ray nextRay() {
        return rayBuffer.next();
    }

    /**
     * Get the next free collision results.
     *
     * @return the next free collision results.
     */
    @FromAnyThread
    public @NotNull CollisionResults nextCollisionResults() {
        return collisionResultsBuffer.next();
    }

    /**
     * Get the next free rotation.
     *
     * @return the next free rotation.
     */
    @FromAnyThread
    public @NotNull Quaternion nextRotation() {
        return rotationBuffer.next();
    }

    /**
     * Get the next free vector.
     *
     * @return the next free vector.
     */
    @FromAnyThread
    public @NotNull Vector3f nextVector() {
        return vectorBuffer.next();
    }

    /**
     * Get the next free vector with values from the source vector.
     *
     * @param vector3f the source vector.
     * @return the next free vector with values from the source vector.
     */
    @FromAnyThread
    public @NotNull Vector3f nextVector(@NotNull final Vector3f vector3f) {
        return vectorBuffer.next().set(vector3f);
    }

    /**
     * Get the next free vector with the values.
     *
     * @param x the X value.
     * @param y the Y value.
     * @param z the Z value.
     * @return the next free vector with the values.
     */
    @FromAnyThread
    public @NotNull Vector3f nextVector(final float x, final float y, final float z) {
        return vectorBuffer.next().set(x, y, z);
    }

    /**
     * Get the next free vector.
     *
     * @return the next free vector.
     */
    @FromAnyThread
    public @NotNull Vector2f nextVector2f() {
        return vector2fBuffer.next();
    }

    /**
     * Get the next free vector with the values.
     *
     * @param x the X value.
     * @param y the Y value.
     * @return the next free vector with the values.
     */
    @FromAnyThread
    public @NotNull Vector2f nextVector(final float x, final float y) {
        return vector2fBuffer.next().set(x, y);
    }

    /**
     * Get the next free plane.
     *
     * @return the next free plane.
     */
    @FromAnyThread
    public @NotNull Plane nextPlane() {
        return planeBuffer.next();
    }

    /**
     * Get the next color.
     *
     * @return the next color.
     */
    @FromAnyThread
    public @NotNull ColorRGBA nextColor() {
        return colorBuffer.next();
    }
}
