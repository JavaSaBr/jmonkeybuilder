package com.ss.editor.ui.component.editing.terrain.control;

import static java.util.Objects.requireNonNull;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * The base implementation of tool control to change height of terrain.
 *
 * @author JavaSaBr
 */
public class ChangeHeightTerrainToolControl extends TerrainToolControl {

    private static class HeightPoint {

        private final float x;
        private final float y;

        private final int xIndex;
        private final int yIndex;

        /**
         * Instantiates a new Height point.
         *
         * @param x      the x
         * @param y      the y
         * @param xIndex the x index
         * @param yIndex the y index
         */
        public HeightPoint(final float x, final float y, final int xIndex, final int yIndex) {
            this.x = x;
            this.y = y;
            this.xIndex = xIndex;
            this.yIndex = yIndex;
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
            int result = xIndex;
            result = 31 * result + yIndex;
            return result;
        }
    }

    /**
     * The table of original heights.
     */
    @NotNull
    private final ObjectDictionary<HeightPoint, Float> originalHeight;

    /**
     * The copy of an original terrain.
     */
    @Nullable
    private Spatial copiedTerrain;

    /**
     * Instantiates a new Change height terrain tool control.
     *
     * @param component the component
     */
    public ChangeHeightTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
        this.originalHeight = DictionaryFactory.newObjectDictionary();
    }

    /**
     * @return the table of original heights.
     */
    @NotNull
    private ObjectDictionary<HeightPoint, Float> getOriginalHeight() {
        return originalHeight;
    }

    /**
     * Start making changes.
     */
    protected void startChange() {

        final ObjectDictionary<HeightPoint, Float> originalHeight = getOriginalHeight();
        originalHeight.clear();

        copiedTerrain = requireNonNull(getEditedModel()).clone();
    }

    /**
     * Notify about wanting to change height of a point.
     *
     * @param point the point.
     */
    protected void change(@NotNull final Vector2f point) {

        final Terrain terrain = (Terrain) requireNonNull(copiedTerrain);
        final Node terrainNode = (Node) requireNonNull(getEditedModel());
        final Vector3f scale = terrainNode.getWorldScale();

        final int halfSize = terrain.getTerrainSize() / 2;
        final int x = Math.round((point.x / scale.x) + halfSize);
        final int z = Math.round((point.y / scale.z) + halfSize);

        final HeightPoint heightPoint = new HeightPoint(point.getX(), point.getY(), x, z);

        final ObjectDictionary<HeightPoint, Float> originalHeight = getOriginalHeight();
        if(originalHeight.containsKey(heightPoint)) return;

        final float height = terrain.getHeightmapHeight(point);

        originalHeight.put(heightPoint, height);
    }

    /**
     * Commit all changes.
     */
    protected void commitChanges() {

        final Terrain terrain = (Terrain) requireNonNull(getEditedModel());
        final ObjectDictionary<Vector2f, Float> oldValues = DictionaryFactory.newObjectDictionary();
        final ObjectDictionary<Vector2f, Float> newValues = DictionaryFactory.newObjectDictionary();

        final ObjectDictionary<HeightPoint, Float> originalHeight = getOriginalHeight();
        originalHeight.forEach((heightPoint, height) -> oldValues.put(new Vector2f(heightPoint.x, heightPoint.y), height));
        originalHeight.forEach((heightPoint, value) -> {
            final Vector2f point = new Vector2f(heightPoint.x, heightPoint.y);
            newValues.put(point, terrain.getHeightmapHeight(point));
        });

        final ModelPropertyOperation<Terrain, ObjectDictionary<Vector2f, Float>> operation =
                new ModelPropertyOperation<>(terrain, "Heightmap", newValues, oldValues);

        operation.setApplyHandler((toChange, heightMap) -> {

            final List<Vector2f> points = new ArrayList<>(heightMap.size());
            final List<Float> heights = new ArrayList<>(heightMap.size());

            heightMap.forEach((point, height) -> {
                points.add(point);
                heights.add(height);
            });

            toChange.setHeight(points, heights);
            ((Node) toChange).updateModelBound();
        });

        originalHeight.clear();

        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        changeConsumer.execute(operation);

        copiedTerrain = null;
    }
}
