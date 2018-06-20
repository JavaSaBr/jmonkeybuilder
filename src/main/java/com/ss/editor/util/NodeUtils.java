package com.ss.editor.util;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.jme3.audio.AudioNode;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.DFSMode;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The class with utility methods for working with {@link Node}.
 *
 * @author JavaSaBr
 */
public class NodeUtils {

    private static final Field FIELD_WORLD_BOUND;

    static {
        try {
            FIELD_WORLD_BOUND = Spatial.class.getDeclaredField("worldBound");
            FIELD_WORLD_BOUND.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find a parent of the model.
     *
     * @param <T>       the node's type.
     * @param spatial   the spatial.
     * @param condition the condition.
     * @return the found parent or null.
     */
    @FromAnyThread
    public static <T> @Nullable T findParent(@NotNull Spatial spatial, @NotNull Predicate<Spatial> condition) {

        if (condition.test(spatial)) {
            return unsafeCast(spatial);
        }

        var parent = spatial.getParent();
        if (parent == null) {
            return null;
        }

        return findParent(parent, condition);
    }

    /**
     * Find a parent of the model.
     *
     * @param <T>       the node's type.
     * @param spatial   the spatial.
     * @param condition the condition.
     * @return the optional result.
     */
    @FromAnyThread
    public static <T> @NotNull Optional<T> findParentOpt(
            @NotNull Spatial spatial,
            @NotNull Predicate<Spatial> condition
    ) {

        if (condition.test(spatial)) {
            return Optional.of(unsafeCast(spatial));
        }

        var parent = spatial.getParent();
        if (parent == null) {
            return Optional.empty();
        }

        return findParentOpt(parent, condition);
    }

    /**
     * Find a parent of the model by the steps.
     *
     * @param spatial the spatial.
     * @param count   the count of steps.
     * @return the result parent.
     */
    @FromAnyThread
    public static @Nullable Spatial findParent(@NotNull Spatial spatial, int count) {

        Spatial parent = spatial;

        while (count-- > 0 && parent != null) {
            parent = parent.getParent();
        }

        return parent;
    }

    /**
     * Find a first geometry in the {@link Spatial}.
     *
     * @param spatial the spatial.
     * @return the geometry or null.
     */
    @FromAnyThread
    public static @Nullable Geometry findGeometry(@NotNull Spatial spatial) {

        if (spatial instanceof Geometry) {
            return (Geometry) spatial;
        } else if (!(spatial instanceof Node)) {
            return null;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            var geometry = findGeometry(children);
            if (geometry != null) {
                return geometry;
            } else if (children instanceof Geometry) {
                return (Geometry) children;
            }
        }

        return null;
    }

    /**
     * Find a first geometry in the {@link Spatial}.
     *
     * @param spatial the spatial.
     * @param name    the name.
     * @return the geometry or null.
     */
    @FromAnyThread
    public static @Nullable Geometry findGeometry(@NotNull Spatial spatial, @NotNull String name) {

        if (!(spatial instanceof Node)) {
            return null;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {

            var geometry = findGeometry(children, name);
            if (geometry != null) {
                return geometry;
            }

            if (children instanceof Geometry && StringUtils.equals(children.getName(), name)) {
                return (Geometry) children;
            }
        }

        return null;
    }

    /**
     * Find a first geometry in the {@link Spatial}.
     *
     * @param spatial   the spatial.
     * @param condition the condition.
     * @return the geometry or null.
     */
    @FromAnyThread
    public static @Nullable Geometry findGeometry(@NotNull Spatial spatial, @NotNull Predicate<Geometry> condition) {

        if (!(spatial instanceof Node)) {
            return null;
        }

        var node = (Node) spatial;

        for (var child : node.getChildren()) {

            var geometry = findGeometry(child, condition);
            if (geometry != null) {
                return geometry;
            }

            if (child instanceof Geometry && condition.test((Geometry) child)) {
                return (Geometry) child;
            }
        }

        return null;
    }

    /**
     * Find a material in the {@link Spatial}.
     *
     * @param spatial   the spatial.
     * @param condition the condition.
     * @return the material or null.
     */
    @FromAnyThread
    public static @Nullable Material findMateial(@NotNull Spatial spatial, @NotNull Predicate<Material> condition) {

        if (!(spatial instanceof Node)) {
            return null;
        }

        var node = (Node) spatial;

        for (var child : node.getChildren()) {

            var material = findMateial(child, condition);
            if (material != null) {
                return material;
            }

            if (child instanceof Geometry && condition.test(((Geometry) child).getMaterial())) {
                return ((Geometry) child).getMaterial();
            }
        }

        return null;
    }

    /**
     * Find a first spatial in the spatial by the name.
     *
     * @param spatial the spatial.
     * @param name    the name.
     * @return the spatial or null.
     */
    @FromAnyThread
    public static @Nullable Spatial findSpatial(@NotNull Spatial spatial, @NotNull String name) {

        if (!(spatial instanceof Node)) {
            return null;
        }

        return ((Node) spatial).getChild(name);
    }

    /**
     * Find a first spatial in the {@link Spatial}.
     *
     * @param spatial   the spatial.
     * @param condition the condition.
     * @return the spatial.
     */
    @FromAnyThread
    public static @Nullable Spatial findSpatial(@NotNull Spatial spatial, @NotNull Predicate<Spatial> condition) {

        if (condition.test(spatial)) {
            return spatial;
        } else if (!(spatial instanceof Node)) {
            return null;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            var child = findSpatial(children, condition);
            if (child != null) {
                return child;
            }
        }

        return null;
    }

    /**
     * Collect all geometries from the asset path.
     *
     * @param spatial   the spatial.
     * @param container the container.
     * @param assetPath the asset path.
     */
    @FromAnyThread
    public static void addGeometryWithMaterial(
            @NotNull Spatial spatial,
            @NotNull Array<Geometry> container,
            @NotNull String assetPath
    ) {

        if (StringUtils.isEmpty(assetPath)) {
            return;
        }

        if (spatial instanceof Geometry) {

            var geometry = (Geometry) spatial;
            var material = geometry.getMaterial();
            var assetName = material == null ? null : material.getAssetName();

            if (StringUtils.equals(assetName, assetPath)) {
                container.add(geometry);
            }

            return;

        } else if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            addGeometryWithMaterial(children, container, assetPath);
        }
    }

    /**
     * Collect all geometries from the asset path.
     *
     * @param spatial   the spatial.
     * @param container the container.
     * @param assetPath the asset path.
     */
    @FromAnyThread
    public static void addSpatialWithAssetPath(
            @NotNull Spatial spatial,
            @NotNull Array<Spatial> container,
            @NotNull String assetPath
    ) {

        if (StringUtils.isEmpty(assetPath)) {
            return;
        }

        var key = spatial.getKey();

        if (key != null && StringUtils.equals(key.getName(), assetPath)) {
            container.add(spatial);
        }

        if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            addSpatialWithAssetPath(children, container, assetPath);
        }
    }

    /**
     * Visit all geometries.
     *
     * @param spatial  the spatial.
     * @param consumer the consumer.
     */
    @FromAnyThread
    public static void visitGeometry(@NotNull Spatial spatial, @NotNull Consumer<Geometry> consumer) {
        spatial.depthFirstTraversal(sp -> {
            if (sp instanceof Geometry) {
                consumer.accept((Geometry) sp);
            }
        }, DFSMode.PRE_ORDER);
    }

    /**
     * Visit all spatial.
     *
     * @param spatial the spatial.
     * @param handler the handler which should return true if need to visit children of a spatial.
     */
    @FromAnyThread
    public static void visitSpatial(@NotNull Spatial spatial, @NotNull Predicate<Spatial> handler) {

        if (!handler.test(spatial)) {
            return;
        } else if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var child : node.getChildren()) {
            visitSpatial(child, handler);
        }
    }

    /**
     * Visit all materials in the spatial.
     *
     * @param spatial  the spatial.
     * @param consumer the consumer.
     */
    @FromAnyThread
    public static void visitMaterials(@NotNull Spatial spatial, @NotNull Consumer<Material> consumer) {

        if (spatial instanceof Geometry) {
            consumer.accept(((Geometry) spatial).getMaterial());
            return;
        } else if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            visitMaterials(children, consumer);
        }
    }

    /**
     * Visit spatials of the type.
     *
     * @param <T>      the spatial's type.
     * @param spatial  the spatial.
     * @param type     the type.
     * @param consumer the consumer.
     */
    @FromAnyThread
    public static <T extends Spatial> void visitSpatial(
            @NotNull Spatial spatial,
            @NotNull Class<T> type,
            @NotNull Consumer<T> consumer
    ) {

        if (type.isInstance(spatial)) {
            consumer.accept(type.cast(spatial));
        }

        if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            visitSpatial(children, type, consumer);
        }
    }

    /**
     * Collect all geometries.
     *
     * @param spatial the spatial.
     * @return the list of all geometries.
     */
    @FromAnyThread
    public static @NotNull Array<Geometry> getGeometries(@NotNull Spatial spatial) {
        var result = ArrayFactory.<Geometry>newArray(Geometry.class);
        addGeometry(spatial, result);
        return result;
    }

    /**
     * Collect all geometries.
     *
     * @param spatial   the spatial.
     * @param container the container.
     */
    @FromAnyThread
    public static void addGeometry(@NotNull Spatial spatial, @NotNull Array<Geometry> container) {

        if (spatial instanceof Geometry) {
            container.add((Geometry) spatial);
            return;
        } else if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            addGeometry(children, container);
        }
    }

    /**
     * Collect all lights.
     *
     * @param spatial   the spatial.
     * @param container the container.
     */
    @FromAnyThread
    public static void addLight(@NotNull Spatial spatial, @NotNull Array<Light> container) {

        var lightList = spatial.getLocalLightList();
        lightList.forEach(container::add);

        if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            addLight(children, container);
        }
    }

    /**
     * Collect all audio nodes.
     *
     * @param spatial   the spatial.
     * @param container the container.
     */
    @FromAnyThread
    public static void addAudioNodes(@NotNull Spatial spatial, @NotNull Array<AudioNode> container) {

        if (!(spatial instanceof Node)) {
            return;
        }

        var node = (Node) spatial;

        for (var children : node.getChildren()) {
            if (children instanceof AudioNode) {
                container.add((AudioNode) children);
            }
            addAudioNodes(children, container);
        }
    }

    /**
     * Create spatial's stream by the spatial.
     *
     * @param spatial the spatial.
     * @return the spatial's stream.
     */
    @FromAnyThread
    public static Stream<Spatial> children(@NotNull Spatial spatial) {

        var result = ArrayFactory.<Spatial>newArray(Spatial.class);

        visitSpatial(spatial, sp -> {
            result.add(sp);
            return true;
        });

        return result.stream();
    }

    /**
     * Force update world bound of the spatial.
     *
     * @param spatial the spatial.
     */
    @JmeThread
    public static void updateWorldBound(@NotNull Spatial spatial) {
        children(spatial).forEach(sp -> sp.forceRefresh(true, true, false));
        children(spatial).forEach(Spatial::getWorldBound);
    }
}
