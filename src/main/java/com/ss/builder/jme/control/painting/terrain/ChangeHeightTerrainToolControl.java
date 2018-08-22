package com.ss.builder.control.painting.terrain;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.Dictionary;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The base implementation of tool control to change height of terrain.
 *
 * @author JavaSaBr
 */
public class ChangeHeightTerrainToolControl extends TerrainToolControl {

    @NotNull
    private static final Supplier<ObjectDictionary<HeightPoint, Float>> DICTIONARY_FACTORY = () ->
            DictionaryFactory.newObjectDictionary(0.2F, 10000);

    private static class HeightPoint {

        private final float x;
        private final float y;

        private final int xIndex;
        private final int yIndex;

        private final int hash;

        public HeightPoint(final float x, final float y, final int xIndex, final int yIndex) {
            this.x = x;
            this.y = y;
            this.xIndex = xIndex;
            this.yIndex = yIndex;
            this.hash = xIndex | (yIndex << 15);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final HeightPoint that = (HeightPoint) o;
            if (xIndex != that.xIndex) return false;
            return yIndex == that.yIndex;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    /**
     * The table of original heights.
     */
    @NotNull
    private final ObjectDictionary<Terrain, ObjectDictionary<HeightPoint, Float>> originalHeight;

    /**
     * The table with copied of original terrains.
     */
    @NotNull
    private final ObjectDictionary<Terrain, Spatial> copiedTerrains;

    /**
     * The current terrains.
     */
    @NotNull
    private final Array<Terrain> terrains;

    public ChangeHeightTerrainToolControl(@NotNull final TerrainPaintingComponent component) {
        super(component);
        this.originalHeight = DictionaryFactory.newObjectDictionary();
        this.copiedTerrains = DictionaryFactory.newObjectDictionary();
        this.terrains = ArrayFactory.newArray(Terrain.class);
    }

    /**
     * Get the table of original heights.
     *
     * @return the table of original heights.
     */
    @JmeThread
    private @NotNull ObjectDictionary<Terrain, ObjectDictionary<HeightPoint, Float>> getOriginalHeight() {
        return originalHeight;
    }

    /**
     * Get the table with copied of original terrains.
     *
     * @return the table with copied of original terrains.
     */
    @JmeThread
    private @NotNull ObjectDictionary<Terrain, Spatial> getCopiedTerrains() {
        return copiedTerrains;
    }

    /**
     * Get the current terrains.
     *
     * @return the current terrains.
     */
    @JmeThread
    protected @NotNull Array<Terrain> getTerrains() {
        return terrains;
    }

    /**
     * Start making changes.
     */
    @JmeThread
    protected void startChange() {

        final ObjectDictionary<Terrain, ObjectDictionary<HeightPoint, Float>> originalHeight = getOriginalHeight();
        originalHeight.forEach(Dictionary::clear);
        originalHeight.clear();

        final Array<Terrain> terrains = getTerrains();
        terrains.clear();

        NodeUtils.visitSpatial(notNull(getPaintedModel()), spatial -> {
            if (spatial instanceof Terrain) {
                terrains.add((Terrain) spatial);
                return false;
            }
            return true;
        });

        final ObjectDictionary<Terrain, Spatial> copiedTerrains = getCopiedTerrains();
        copiedTerrains.clear();

        terrains.forEach(copiedTerrains, (terrain, toStore) ->
                toStore.put(terrain, ((Spatial) terrain).clone()));
    }

    /**
     * Notify about changing height by the point in the terrain.
     *
     * @param terrain the terrain.
     * @param point   the point.
     */
    @JmeThread
    protected void change(@NotNull final Terrain terrain, @NotNull final Vector2f point) {

        final Node terrainNode = (Node) terrain;
        final Vector3f scale = terrainNode.getWorldScale();

        final int halfSize = terrain.getTerrainSize() / 2;
        final int x = Math.round((point.x / scale.x) + halfSize);
        final int z = Math.round((point.y / scale.z) + halfSize);

        final HeightPoint heightPoint = new HeightPoint(point.getX(), point.getY(), x, z);

        final ObjectDictionary<Terrain, ObjectDictionary<HeightPoint, Float>> originalHeight = getOriginalHeight();
        final ObjectDictionary<HeightPoint, Float> terrainHeights = originalHeight.get(terrain, DICTIONARY_FACTORY);
        if (terrainHeights.containsKey(heightPoint)) {
            return;
        }

        final float height = terrain.getHeightmapHeight(point);
        terrainHeights.put(heightPoint, height);
    }

    /**
     * Commit all changes.
     */
    protected void commitChanges() {

        final Spatial paintedModel = getPaintedModel();
        final ObjectDictionary<Terrain, ObjectDictionary<Vector2f, Float>> oldValues = DictionaryFactory.newObjectDictionary();
        final ObjectDictionary<Terrain, ObjectDictionary<Vector2f, Float>> newValues = DictionaryFactory.newObjectDictionary();

        final ObjectDictionary<Terrain, ObjectDictionary<HeightPoint, Float>> originalHeight = getOriginalHeight();
        originalHeight.forEach((terrain, floats) -> {
            final ObjectDictionary<Vector2f, Float> values = oldValues.get(terrain, () -> createValuesDictionary(floats));
            floats.forEach((heightPoint, height) -> values.put(new Vector2f(heightPoint.x, heightPoint.y), height));
        });

        originalHeight.forEach((terrain, floats) -> {
            final ObjectDictionary<Vector2f, Float> values = newValues.get(terrain, () -> createValuesDictionary(floats));
            floats.forEach((heightPoint, height) -> {
                final Vector2f point = new Vector2f(heightPoint.x, heightPoint.y);
                values.put(point, terrain.getHeightmapHeight(point));
            });
        });

        final Array<Terrain> toApply = ArrayFactory.newArray(Terrain.class);
        toApply.addAll(getTerrains());

        final PropertyOperation<ChangeConsumer, Spatial, ObjectDictionary<Terrain, ObjectDictionary<Vector2f, Float>>> operation =
                new PropertyOperation<>(paintedModel, "Heightmap", newValues, oldValues);

        operation.setApplyHandler((node, heightMaps) -> {

            for (final Terrain terrain : toApply) {

                final ObjectDictionary<Vector2f, Float> heightData = heightMaps.get(terrain);
                if (heightData == null || heightData.isEmpty()) {
                    continue;
                }

                final List<Vector2f> points = new ArrayList<>(heightMaps.size());
                final List<Float> heights = new ArrayList<>(heightMaps.size());

                heightData.forEach((point, height) -> {
                    points.add(point);
                    heights.add(height);
                });

                terrain.setHeight(points, heights);
            }

            node.updateModelBound();
        });

        originalHeight.forEach(Dictionary::clear);
        originalHeight.clear();

        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        changeConsumer.execute(operation);

        getTerrains().clear();
        getCopiedTerrains().clear();
    }

    @FromAnyThread
    private @NotNull ObjectDictionary<Vector2f, Float> createValuesDictionary(@NotNull final ObjectDictionary<HeightPoint, Float> floats) {
        return DictionaryFactory.newObjectDictionary(0.2F, floats.size());
    }
}
