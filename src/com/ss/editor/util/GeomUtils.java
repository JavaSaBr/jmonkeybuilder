package com.ss.editor.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static int getIndex(final Spatial model, final Object object) {

        Spatial parent = model;
        int parentIndex = 0;

        while (parent != null) {

            if (Objects.equals(parent, object)) {
                return parentIndex;
            }

            parent = parent.getParent();
            parentIndex--;
        }

        if (!(model instanceof Node)) {
            return -1;
        }

        final AtomicInteger counter = new AtomicInteger(0);

        final Node node = (Node) model;

        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            if (getIndex(child, object, counter)) {
                return counter.get();
            }
        }

        return -1;
    }

    private static boolean getIndex(final Spatial model, final Object object, final AtomicInteger counter) {
        counter.incrementAndGet();

        if (Objects.equals(model, object)) {
            return true;
        } else if (!(model instanceof Node)) {
            return false;
        }

        final Node node = (Node) model;

        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            if (getIndex(child, object, counter)) {
                return true;
            }
        }

        return false;
    }

    public static Object getObjectByIndex(final Spatial model, final int index) {

        Spatial parent = model;
        int parentIndex = 0;

        while (parent != null) {

            if (parentIndex == index) {
                return parent;
            }

            parent = parent.getParent();
            parentIndex--;
        }

        if (!(model instanceof Node)) {
            return -1;
        }

        if (index == 0) {
            return model;
        } else if (!(model instanceof Node)) {
            return null;
        }

        final AtomicInteger counter = new AtomicInteger(0);
        final Node node = (Node) model;

        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {

            final Object object = getObjectByIndex(child, index, counter);

            if (object != null) {
                return object;
            }
        }

        return null;
    }

    private static Object getObjectByIndex(final Spatial model, final int index, final AtomicInteger counter) {

        if (counter.incrementAndGet() == index) {
            return model;
        } else if (!(model instanceof Node)) {
            return null;
        }

        final Node node = (Node) model;

        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {

            final Object object = getObjectByIndex(child, index, counter);

            if (object != null) {
                return object;
            }
        }

        return null;
    }

    public static boolean canAttach(final Node node, final Spatial spatial) {

        Spatial parent = node;

        while (parent != null) {

            if (parent == spatial) {
                return false;
            }

            parent = parent.getParent();
        }

        return true;
    }
}
