package com.ss.editor.util;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class with utility methods for working with geometry.
 *
 * @author JavaSaBr.
 */
public class GeomUtils {

    /**
     * Get the UP vector from the rotation.
     *
     * @param rotation the rotation
     * @param store    the store
     * @return the up
     */
    @NotNull
    public static Vector3f getUp(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(1, store);
    }

    /**
     * Get the Left vector from the rotation.
     *
     * @param rotation the rotation
     * @param store    the store
     * @return the left
     */
    @NotNull
    public static Vector3f getLeft(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(0, store);
    }

    /**
     * Get the Direction vector from the rotation.
     *
     * @param rotation the rotation
     * @param store    the store
     * @return the direction
     */
    @NotNull
    public static Vector3f getDirection(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(2, store);
    }

    /**
     * Get the index of the object in the model.
     *
     * @param model  the model
     * @param object the object
     * @return the index
     */
    public static int getIndex(@NotNull final Spatial model, @NotNull final Object object) {

        Spatial parent = model;
        int parentIndex = 0;

        while (parent != null) {
            if (Objects.equals(parent, object)) return parentIndex;
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
            if (getIndex(child, object, counter)) return counter.get();
        }

        return -1;
    }

    /**
     * Get the index of the object in the model.
     */
    private static boolean getIndex(@NotNull final Object model, @NotNull final Object object,
                                    @NotNull final AtomicInteger counter) {
        counter.incrementAndGet();

        if (Objects.equals(model, object)) {
            return true;
        } else if (model instanceof Geometry) {
            return getIndex(((Geometry) model).getMesh(), object, counter);
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

    /**
     * Find the object by the index in the model.
     *
     * @param model the model
     * @param index the index
     * @return the object by index
     */
    @Nullable
    public static Object getObjectByIndex(@NotNull final Spatial model, final int index) {

        Spatial parent = model;
        int parentIndex = 0;

        while (parent != null) {
            if (parentIndex == index) return parent;
            parent = parent.getParent();
            parentIndex--;
        }

        if (!(model instanceof Node)) {
            return null;
        }

        final AtomicInteger counter = new AtomicInteger(0);
        final Node node = (Node) model;

        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            final Object object = getObjectByIndex(child, index, counter);
            if (object != null) return object;
        }

        return null;
    }

    /**
     * Find the object by the index in the model.
     */
    @Nullable
    private static Object getObjectByIndex(@NotNull final Object model, final int index,
                                           @NotNull final AtomicInteger counter) {

        if (counter.incrementAndGet() == index) {
            return model;
        } else if (model instanceof Geometry) {
            return getObjectByIndex(((Geometry) model).getMesh(), index, counter);
        } else if (!(model instanceof Node)) {
            return null;
        }

        final Node node = (Node) model;
        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            final Object object = getObjectByIndex(child, index, counter);
            if (object != null) return object;
        }

        return null;
    }

    /**
     * Can attach boolean.
     *
     * @param node    the node
     * @param spatial the spatial
     * @return true if the spatial can be attached to the node.
     */
    public static boolean canAttach(@NotNull final Node node, @NotNull final Spatial spatial) {

        Spatial parent = node;

        while (parent != null) {
            if (parent == spatial) return false;
            parent = parent.getParent();
        }

        return true;
    }

    /**
     * Get a context point on spatial from cursor position.
     *
     * @param spatial the spatial.
     * @return the contact point or null.
     */
    @Nullable
    public static Vector3f getContactPointFromCursor(@NotNull final Spatial spatial) {

        final Editor editor = Editor.getInstance();
        final InputManager inputManager = editor.getInputManager();
        final Vector2f cursor = inputManager.getCursorPosition();

        return getContactPointFromScreenPos(spatial, cursor.getX(), cursor.getY());
    }

    /**
     * Get a context point on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the contact point or null.
     */
    @Nullable
    public static Vector3f getContactPointFromScreenPos(@NotNull final Spatial spatial, final float screenX,
                                                        final float screenY) {
        final CollisionResult collision = getCollisionFromScreenPos(spatial, screenX, screenY);
        return collision == null ? null : collision.getContactPoint();
    }

    /**
     * Get a geometry on spatial from cursor position.
     *
     * @param spatial the spatial.
     * @return the geometry or null.
     */
    @Nullable
    public static Geometry getGeometryFromCursor(@NotNull final Spatial spatial) {

        final Editor editor = Editor.getInstance();
        final InputManager inputManager = editor.getInputManager();
        final Vector2f cursor = inputManager.getCursorPosition();

        return getGeometryFromScreenPos(spatial, cursor.getX(), cursor.getY());
    }

    /**
     * Get a geometry on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the geometry or null.
     */
    @Nullable
    public static Geometry getGeometryFromScreenPos(@NotNull final Spatial spatial, final float screenX,
                                                    final float screenY) {
        final CollisionResult collision = getCollisionFromScreenPos(spatial, screenX, screenY);
        return collision == null ? null : collision.getGeometry();
    }

    /**
     * Get a collision on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the collision or null.
     */
    @Nullable
    public static CollisionResult getCollisionFromScreenPos(@NotNull final Spatial spatial, final float screenX,
                                                            final float screenY) {

        final Editor editor = Editor.getInstance();
        final Camera camera = editor.getCamera();

        final Vector2f cursor = new Vector2f(screenX, screenY);
        final Vector3f click3d = camera.getWorldCoordinates(cursor, 0f);
        final Vector3f dir = camera.getWorldCoordinates(cursor, 1f)
                .subtractLocal(click3d)
                .normalizeLocal();

        final Ray ray = new Ray();
        ray.setOrigin(click3d);
        ray.setDirection(dir);

        final CollisionResults results = new CollisionResults();

        spatial.updateModelBound();
        spatial.collideWith(ray, results);

        if (results.size() < 1) {
            return null;
        }

        return results.getClosestCollision();
    }
}
