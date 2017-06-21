package com.ss.editor.util;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.asset.AssetKey;
import com.jme3.audio.AudioNode;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The class with utility methods for working with {@link Node}.
 *
 * @author JavaSaBr
 */
public class NodeUtils {

    /**
     * Find the parent of the model.
     *
     * @param <T>       the type parameter
     * @param spatial   the spatial
     * @param condition the condition
     * @return the t
     */
    @Nullable
    public static <T> T findParent(@NotNull final Spatial spatial, @NotNull final Predicate<Spatial> condition) {
        if (condition.test(spatial)) return unsafeCast(spatial);
        final Node parent = spatial.getParent();
        if (parent == null) return null;
        return findParent(parent, condition);
    }

    /**
     * Find the parent of the model.
     *
     * @param spatial the spatial
     * @param count   the count
     * @return the spatial
     */
    @Nullable
    public static Spatial findParent(@NotNull final Spatial spatial, int count) {

        Spatial parent = spatial;

        while (count-- > 0 && parent != null) {
            parent = parent.getParent();
        }

        return parent;
    }

    /**
     * Find a first geometry in the {@link Spatial}.
     *
     * @param spatial the spatial
     * @return the geometry
     */
    @Nullable
    public static Geometry findGeometry(@NotNull final Spatial spatial) {
        if (!(spatial instanceof Node)) return null;

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            final Geometry geometry = findGeometry(children);
            if (geometry != null) return geometry;
            if (children instanceof Geometry) return (Geometry) children;
        }

        return null;
    }

    /**
     * Find a first geometry in the {@link Spatial}.
     *
     * @param spatial the spatial
     * @param name    the name
     * @return the geometry
     */
    @Nullable
    public static Geometry findGeometry(@NotNull final Spatial spatial, @NotNull final String name) {
        if (!(spatial instanceof Node)) return null;

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            final Geometry geometry = findGeometry(children, name);
            if (geometry != null) return geometry;
            if (children instanceof Geometry && StringUtils.equals(children.getName(), name)) {
                return (Geometry) children;
            }
        }

        return null;
    }

    /**
     * Find a first spatial in the {@link Spatial}.
     *
     * @param spatial the spatial
     * @param name    the name
     * @return the spatial
     */
    @Nullable
    public static Spatial findSpatial(@NotNull final Spatial spatial, @NotNull final String name) {
        if (!(spatial instanceof Node)) return null;
        return ((Node) spatial).getChild(name);
    }

    /**
     * Find a first spatial in the {@link Spatial}.
     *
     * @param spatial   the spatial
     * @param condition the condition
     * @return the spatial
     */
    @Nullable
    public static Spatial findSpatial(@NotNull final Spatial spatial, @NotNull final Predicate<Spatial> condition) {
        if (condition.test(spatial)) return spatial;
        if (!(spatial instanceof Node)) return null;

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            final Spatial subSpatial = findSpatial(children, condition);
            if (subSpatial != null) return subSpatial;
        }

        return null;
    }

    /**
     * Collect all geometries from the asset path.
     *
     * @param spatial   the spatial
     * @param container the container
     * @param assetPath the asset path
     */
    public static void addGeometryWithMaterial(@NotNull final Spatial spatial, @NotNull final Array<Geometry> container,
                                               @NotNull final String assetPath) {
        if (StringUtils.isEmpty(assetPath)) return;

        if (spatial instanceof Geometry) {

            final Geometry geometry = (Geometry) spatial;
            final Material material = geometry.getMaterial();
            final String assetName = material == null ? null : material.getAssetName();

            if (StringUtils.equals(assetName, assetPath)) {
                container.add(geometry);
            }

            return;

        } else if (!(spatial instanceof Node)) {
            return;
        }

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            addGeometryWithMaterial(children, container, assetPath);
        }
    }

    /**
     * Collect all geometries from the asset path.
     *
     * @param spatial   the spatial
     * @param container the container
     * @param assetPath the asset path
     */
    public static void addSpatialWithAssetPath(@NotNull final Spatial spatial, @NotNull final Array<Spatial> container,
                                               @NotNull final String assetPath) {
        if (StringUtils.isEmpty(assetPath)) return;

        final AssetKey key = spatial.getKey();

        if (key != null && StringUtils.equals(key.getName(), assetPath)) {
            container.add(spatial);
        }

        if (!(spatial instanceof Node)) {
            return;
        }

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            addSpatialWithAssetPath(children, container, assetPath);
        }
    }

    /**
     * Visit all geometries.
     *
     * @param spatial  the spatial
     * @param consumer the consumer
     */
    public static void visitGeometry(@NotNull final Spatial spatial, @NotNull final Consumer<Geometry> consumer) {

        if (spatial instanceof Geometry) {
            consumer.accept((Geometry) spatial);
            return;
        } else if (!(spatial instanceof Node)) {
            return;
        }

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            visitGeometry(children, consumer);
        }
    }

    /**
     * Visit spatials of the target type.
     *
     * @param <T>      the type parameter
     * @param spatial  the spatial
     * @param type     the type
     * @param consumer the consumer
     */
    public static <T extends Spatial> void visitSpatial(@NotNull final Spatial spatial, @NotNull final Class<T> type,
                                                        @NotNull final Consumer<T> consumer) {

        if (type.isInstance(spatial)) {
            consumer.accept(type.cast(spatial));
            return;
        } else if (!(spatial instanceof Node)) {
            return;
        }

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            visitSpatial(children, type, consumer);
        }
    }

    /**
     * Collect all geometries.
     *
     * @param spatial   the spatial
     * @param container the container
     */
    public static void addGeometry(@NotNull final Spatial spatial, @NotNull final Array<Geometry> container) {

        if (spatial instanceof Geometry) {
            container.add((Geometry) spatial);
            return;
        } else if (!(spatial instanceof Node)) {
            return;
        }

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            addGeometry(children, container);
        }
    }

    /**
     * Collect all lights.
     *
     * @param spatial   the spatial
     * @param container the container
     */
    public static void addLight(@NotNull final Spatial spatial, @NotNull final Array<Light> container) {

        final LightList lightList = spatial.getLocalLightList();
        lightList.forEach(container::add);

        if (!(spatial instanceof Node)) return;

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            addLight(children, container);
        }
    }

    /**
     * Collect all audio nodes.
     *
     * @param spatial   the spatial
     * @param container the container
     */
    public static void addAudioNodes(@NotNull final Spatial spatial, @NotNull final Array<AudioNode> container) {
        if (!(spatial instanceof Node)) return;

        final Node node = (Node) spatial;

        for (final Spatial children : node.getChildren()) {
            if (children instanceof AudioNode) {
                container.add((AudioNode) children);
            }
            addAudioNodes(children, container);
        }
    }
}
