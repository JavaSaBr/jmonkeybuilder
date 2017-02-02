package com.ss.editor.util;

import static java.lang.Thread.currentThread;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.ss.editor.EditorThread;

import org.jetbrains.annotations.NotNull;

import rlib.util.CycleBuffer;

/**
 * The container with local objects.
 *
 * @author JavaSaBr.
 */
public class LocalObjects {

    private static final int SIZE = 20;

    @NotNull
    public static LocalObjects get() {
        return ((EditorThread) currentThread()).getLocal();
    }

    /**
     * The buffer of vectors.
     */
    @NotNull
    private final CycleBuffer<Vector3f> vectorBuffer;

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
     * The buffer of matrixes.
     */
    @NotNull
    private final CycleBuffer<Matrix3f> matrix3fBuffer;

    /**
     * The buffer of matrix float arrays.
     */
    @NotNull
    private final CycleBuffer<float[]> matrixFloatBuffer;

    @SuppressWarnings("unchecked")
    public LocalObjects() {
        this.vectorBuffer = new CycleBuffer<>(Vector3f.class, SIZE, Vector3f::new);
        this.rotationBuffer = new CycleBuffer<>(Quaternion.class, SIZE, Quaternion::new);
        this.rayBuffer = new CycleBuffer<>(Ray.class, SIZE, Ray::new);
        this.matrix3fBuffer = new CycleBuffer<>(Matrix3f.class, SIZE, Matrix3f::new);
        this.matrixFloatBuffer = new CycleBuffer<>(float[].class, SIZE, () -> new float[16]);
    }

    /**
     * @return the next free matrix.
     */
    @NotNull
    public Matrix3f getNextMatrix3f() {
        return matrix3fBuffer.next();
    }

    /**
     * @return the next free matrix float array.
     */
    @NotNull
    public float[] getNextMatrixFloat() {
        return matrixFloatBuffer.next();
    }

    /**
     * @return the next free ray.
     */
    @NotNull
    public Ray getNextRay() {
        return rayBuffer.next();
    }

    /**
     * @return the next free rotation.
     */
    @NotNull
    public Quaternion getNextRotation() {
        return rotationBuffer.next();
    }

    /**
     * @return the next free vector.
     */
    @NotNull
    public Vector3f getNextVector() {
        return vectorBuffer.next();
    }
}
