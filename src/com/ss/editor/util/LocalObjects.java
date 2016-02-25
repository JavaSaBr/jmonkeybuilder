package com.ss.editor.util;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.ss.editor.EditorThread;


/**
 * Контейнер локальных объектов.
 *
 * @author Ronn
 */
public class LocalObjects {

    private static final int SIZE = 20;
    private static final int LIMIT = SIZE - 1;

    public static final LocalObjects get() {
        return ((EditorThread) Thread.currentThread()).getLocalObects();
    }

    /**
     * Буффер векторов.
     */
    private final Vector3f[] vectorBuffer;

    /**
     * Буфер кватернионов.
     */
    private final Quaternion[] rotationBuffer;

    /**
     * Буфер лучей.
     */
    private final Ray[] rayBuffer;

    /**
     * Буффер матриц 3f.
     */
    private final Matrix3f[] matrix3fBuffer;

    /**
     * Буффер массивов данных матриц.
     */
    private final float[][] matrixFloatBuffer;

    /**
     * Индекс след. свободного вектора.
     */
    private int vectorIndex;

    /**
     * Иднекс след. свободного кватерниона.
     */
    private int rotationIndex;

    /**
     * Индекс следующего луча.
     */
    private int rayIndex;

    /**
     * Индекс следующей матрицы.
     */
    private int matrix3fIndex;

    /**
     * Индекс следующего массива данных матрицы.
     */
    private int matrixFloatIndex;

    @SuppressWarnings("unchecked")
    public LocalObjects() {
        this.vectorBuffer = new Vector3f[SIZE];
        this.rotationBuffer = new Quaternion[SIZE];
        this.rayBuffer = new Ray[SIZE];
        this.matrix3fBuffer = new Matrix3f[SIZE];
        this.matrixFloatBuffer = new float[SIZE][];

        for (int i = 0, length = SIZE; i < length; i++) {
            rotationBuffer[i] = new Quaternion();
            vectorBuffer[i] = new Vector3f();
            rayBuffer[i] = new Ray();
            matrix3fBuffer[i] = new Matrix3f();
            matrixFloatBuffer[i] = new float[16];
        }
    }

    /**
     * @return получаем след. свободную матрицу 3f.
     */
    public Matrix3f getNextMatrix3f() {

        if (matrix3fIndex == LIMIT) {
            matrix3fIndex = 0;
        }

        return matrix3fBuffer[matrix3fIndex++];
    }

    /**
     * @return получаем след. массив данных матрицы.
     */
    public float[] getNextMatrixFloat() {

        if (matrixFloatIndex == LIMIT) {
            matrixFloatIndex = 0;
        }

        return matrixFloatBuffer[matrixFloatIndex++];
    }

    /**
     * @return получаем след. свободного луча.
     */
    public Ray getNextRay() {

        if (rayIndex == LIMIT) {
            rayIndex = 0;
        }

        return rayBuffer[rayIndex++];
    }

    /**
     * @return получаем след. свободный квантернион.
     */
    public Quaternion getNextRotation() {

        if (rotationIndex == LIMIT) {
            rotationIndex = 0;
        }

        return rotationBuffer[rotationIndex++];
    }

    /**
     * @return получаем след. свободнвый вектор.
     */
    public Vector3f getNextVector() {

        if (vectorIndex == LIMIT) {
            vectorIndex = 0;
        }

        return vectorBuffer[vectorIndex++];
    }
}
