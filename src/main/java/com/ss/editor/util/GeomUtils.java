package com.ss.editor.util;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
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
     * @param rotation the rotation.
     * @param store    the store.
     * @return the up.
     */
    @FromAnyThread
    public static @NotNull Vector3f getUp(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(1, store);
    }

    /**
     * Get the Left vector from the rotation.
     *
     * @param rotation the rotation.
     * @param store    the store.
     * @return the left.
     */
    @FromAnyThread
    public static @NotNull Vector3f getLeft(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(0, store);
    }

    /**
     * Get the direction vector from the rotation.
     *
     * @param rotation the rotation.
     * @param store    the store.
     * @return the direction.
     */
    @FromAnyThread
    public static @NotNull Vector3f getDirection(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(2, store);
    }

    /**
     * Get the index of the object in the model.
     *
     * @param model  the model.
     * @param object the object.
     * @return the index.
     */
    @FromAnyThread
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
            if (getIndex(child, object, counter)) {
                return counter.get();
            }
        }

        return -1;
    }

    /**
     * Get the index of the object in the model.
     */
    @FromAnyThread
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
     * @param model the model.
     * @param index the index.
     * @return the object by index.
     */
    @FromAnyThread
    public static @Nullable Object getObjectByIndex(@NotNull final Spatial model, final int index) {

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
    @FromAnyThread
    private static @Nullable Object getObjectByIndex(@NotNull final Object model, final int index,
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
     * Check a possibility to attach the spatial to the node.
     *
     * @param node    the node.
     * @param spatial the spatial.
     * @param isCopy  true if the spatial need to copy..
     * @return true if the spatial can be attached to the node.
     */
    @FromAnyThread
    public static boolean canAttach(@NotNull final Node node, @NotNull final Spatial spatial, final boolean isCopy) {

        if (spatial.getParent() == node && !isCopy) {
            return false;
        } else if (node instanceof AssetLinkNode) {
            return false;
        }

        final AssetLinkNode linkNode = isCopy ? null :
                NodeUtils.findParent(spatial.getParent(), AssetLinkNode.class::isInstance);

        return linkNode == null;
    }

    /**
     * Get a contact point on the spatial from cursor position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @return the contact point or null.
     */
    @FromAnyThread
    public static @Nullable Vector3f getContactPointFromCursor(@NotNull final Spatial spatial, @NotNull final Camera camera) {
        final InputManager inputManager = EditorUtil.getInputManager();
        final Vector2f cursor = inputManager.getCursorPosition();
        return getContactPointFromScreenPos(spatial, camera, cursor.getX(), cursor.getY());
    }

    /**
     * Get a collision result on the spatial from cursor position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @return the collision result or null.
     */
    @FromAnyThread
    public static @Nullable CollisionResult getCollisionFromCursor(@NotNull final Spatial spatial,
                                                                   @NotNull final Camera camera) {
        final InputManager inputManager = EditorUtil.getInputManager();
        final Vector2f cursor = inputManager.getCursorPosition();
        return getCollisionFromScreenPos(spatial, camera, cursor.getX(), cursor.getY());
    }

    /**
     * Get a collisions result on the spatial from cursor position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @return the collisions result.
     */
    @FromAnyThread
    public static @NotNull CollisionResults getCollisionsFromCursor(@NotNull final Spatial spatial,
                                                                    @NotNull final Camera camera) {
        final InputManager inputManager = EditorUtil.getInputManager();
        final Vector2f cursor = inputManager.getCursorPosition();
        return getCollisionsFromScreenPos(spatial, camera, cursor.getX(), cursor.getY());
    }

    /**
     * Get a contact point on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the contact point or null.
     */
    @FromAnyThread
    public static @Nullable Vector3f getContactPointFromScreenPos(@NotNull final Spatial spatial,
                                                                  @NotNull final Camera camera, final float screenX,
                                                                  final float screenY) {
        final CollisionResult collision = getCollisionFromScreenPos(spatial, camera, screenX, screenY);
        return collision == null ? null : collision.getContactPoint();
    }

    /**
     * Get a contact normal on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the contact normal or null.
     */
    @FromAnyThread
    public static @Nullable Vector3f getContactNormalFromScreenPos(@NotNull final Spatial spatial,
                                                                   @NotNull final Camera camera, final float screenX,
                                                                   final float screenY) {
        final CollisionResult collision = getCollisionFromScreenPos(spatial, camera, screenX, screenY);
        return collision == null ? null : collision.getContactNormal();
    }

    /**
     * Get a geometry on spatial from cursor position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @return the geometry or null.
     */
    @FromAnyThread
    public static @Nullable Geometry getGeometryFromCursor(@NotNull final Spatial spatial, @NotNull final Camera camera) {
        final InputManager inputManager = EditorUtil.getInputManager();
        final Vector2f cursor = inputManager.getCursorPosition();
        return getGeometryFromScreenPos(spatial, camera, cursor.getX(), cursor.getY());
    }

    /**
     * Get a geometry on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param camera the camera.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the geometry or null.
     */
    @FromAnyThread
    public static @Nullable Geometry getGeometryFromScreenPos(@NotNull final Spatial spatial,
                                                              @NotNull final Camera camera, final float screenX,
                                                              final float screenY) {
        final CollisionResult collision = getCollisionFromScreenPos(spatial, camera, screenX, screenY);
        return collision == null ? null : collision.getGeometry();
    }

    /**
     * Get a collision on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the collision or null.
     */
    @FromAnyThread
    public static @Nullable CollisionResult getCollisionFromScreenPos(@NotNull final Spatial spatial,
                                                                      @NotNull final Camera camera, final float screenX,
                                                                      final float screenY) {

        final CollisionResults results = getCollisionsFromScreenPos(spatial, camera, screenX, screenY);
        if (results.size() < 1) {
            return null;
        }

        return results.getClosestCollision();
    }

    /**
     * Get a collision on spatial from screen position.
     *
     * @param spatial the spatial.
     * @param camera  the camera.
     * @param screenX the screen X coord.
     * @param screenY the screen Y coord.
     * @return the collisions .
     */
    @FromAnyThread
    public static @NotNull CollisionResults getCollisionsFromScreenPos(@NotNull final Spatial spatial,
                                                                       @NotNull final Camera camera,
                                                                       final float screenX, final float screenY) {

        final LocalObjects local = LocalObjects.get();

        final Vector2f cursor = local.nextVector(screenX, screenY);
        final Vector3f click3d = camera.getWorldCoordinates(cursor, 0f, local.nextVector());
        final Vector3f dir = camera.getWorldCoordinates(cursor, 1f, local.nextVector())
                .subtractLocal(click3d)
                .normalizeLocal();

        final Ray ray = local.nextRay();
        ray.setOrigin(click3d);
        ray.setDirection(dir);

        final CollisionResults results = local.nextCollisionResults();

        spatial.updateModelBound();
        spatial.collideWith(ray, results);

        return results;
    }

    /**
     * Compare the vector with coordinates.
     *
     * @param vector the vector.
     * @param x the X coordinate.
     * @param y the Y coordinate.
     * @param z the Z coordinate.
     * @return true if the vector is equal for the coordinates.
     */
    @FromAnyThread
    public static boolean equals(@Nullable Vector3f vector, float x, float y, float z) {

        if (vector == null) {
            return false;
        } else if (Float.compare(x, x) != 0) {
            return false;
        } else if (Float.compare(y, y) != 0) {
            return false;
        } else {
            return Float.compare(z, z) == 0;
        }
    }

    /**
     * Return {@link Vector3f#ZERO} if the vector is null or the same vector.
     *
     * @param vector the vector.
     * @return {@link Vector3f#ZERO} if the vector is null or the same vector.
     */
    public static @NotNull Vector3f zeroIfNull(@Nullable Vector3f vector) {
        return vector == null ? Vector3f.ZERO : vector;
    }
}
