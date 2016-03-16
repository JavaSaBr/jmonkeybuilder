package com.ss.editor.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Набор утильных методов по работе с геометрией.
 *
 * @author Ronn
 */
public class GeomUtils {

    public static Vector3f getUp(final Quaternion rotation, final Vector3f store) {
        return rotation.getRotationColumn(1, store);
    }

    public static Vector3f getLeft(final Quaternion rotation, final Vector3f store) {
        return rotation.getRotationColumn(0, store);
    }

    public static Vector3f getDirection(final Quaternion rotation, final Vector3f store) {
        return rotation.getRotationColumn(2, store);
    }
}
