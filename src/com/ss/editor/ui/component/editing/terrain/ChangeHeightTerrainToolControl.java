package com.ss.editor.ui.component.editing.terrain;

import static java.util.Objects.requireNonNull;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * The base implementation of tool control to change height of terrain.
 *
 * @author JavaSaBr
 */
public class ChangeHeightTerrainToolControl extends TerrainToolControl {

    /**
     * The table of original heights.
     */
    @NotNull
    private final ObjectDictionary<Vector2f, Float> originalHeight;

    /**
     * The copy of an original terrain.
     */
    @Nullable
    private Spatial copiedTerrain;

    public ChangeHeightTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
        this.originalHeight = DictionaryFactory.newObjectDictionary();
    }

    /**
     * @return the table of original heights.
     */
    @NotNull
    private ObjectDictionary<Vector2f, Float> getOriginalHeight() {
        return originalHeight;
    }

    /**
     * Start making changes.
     */
    protected void startChange() {

        final ObjectDictionary<Vector2f, Float> originalHeight = getOriginalHeight();
        originalHeight.clear();

        copiedTerrain = requireNonNull(getEditedModel()).clone();
    }

    /**
     * Notify about wanting to change height of a point.
     *
     * @param point the point.
     */
    protected void change(@NotNull final Vector2f point) {

        final ObjectDictionary<Vector2f, Float> originalHeight = getOriginalHeight();
        if(originalHeight.containsKey(point)) return;

        final Terrain terrain = (Terrain) requireNonNull(copiedTerrain);
        final float height = terrain.getHeightmapHeight(point);

        originalHeight.put(point, height);
    }

    /**
     * Commit all changes.
     */
    protected void commitChanges() {

        final ObjectDictionary<Vector2f, Float> originalHeight = getOriginalHeight();

        final ObjectDictionary<Vector2f, Float> oldValues = DictionaryFactory.newObjectDictionary();
        oldValues.put(originalHeight);

        final ObjectDictionary<Vector2f, Float> newValues = DictionaryFactory.newObjectDictionary();
        final Terrain terrain = (Terrain) requireNonNull(getEditedModel());
        originalHeight.forEach((point, value) -> newValues.put(point, terrain.getHeightmapHeight(point)));

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
