package com.ss.editor.ui.component.editing.terrain.control;

import static com.ss.editor.util.EditingUtils.*;
import static java.util.Objects.requireNonNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.Terrain;
import com.ss.editor.control.editing.EditingInput;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of terrain tool to change height by level.
 *
 * @author JavaSaBr
 */
public class LevelTerrainToolControl extends ChangeHeightTerrainToolControl {

    /**
     * The level marker.
     */
    @NotNull
    protected final Geometry levelMarker;

    /**
     * The target height level.
     */
    private float level;

    /**
     * The flag of using marker.
     */
    private boolean useMarker;

    /**
     * The flag of using precision changing.
     */
    private boolean precision;

    /**
     * Instantiates a new Level terrain tool control.
     *
     * @param component the component
     */
    public LevelTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);

        this.levelMarker = new Geometry("LevelMarker", new Sphere(8, 8, 1));
        this.levelMarker.setMaterial(createMaterial(getBrushColor()));
    }

    @Override
    protected void onAttached(@NotNull final Node node) {
        super.onAttached(node);

        final Spatial editedModel = requireNonNull(getEditedModel());
        final Geometry levelMarker = getLevelMarker();

        final Node markersNode = component.getMarkersNode();
        markersNode.attachChild(levelMarker);

        levelMarker.setLocalTranslation(editedModel.getWorldTranslation());
    }

    @Override
    protected void onDetached(@NotNull final Node node) {
        super.onDetached(node);

        final Node markersNode = component.getMarkersNode();
        markersNode.detachChild(getLevelMarker());
    }

    @Override
    protected void controlUpdate(final float tpf) {
        super.controlUpdate(tpf);

        final Geometry levelMarker = getLevelMarker();
        levelMarker.setCullHint(isUseMarker() ? Spatial.CullHint.Never : Spatial.CullHint.Always);
    }

    @NotNull
    @Override
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Red;
    }

    @Override
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        super.startEditing(editingInput, contactPoint);

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                startChange();
                modifyHeight(contactPoint);
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
            }
        }
    }

    @Override
    public void updateEditing(@NotNull final Vector3f contactPoint) {

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
            }
        }
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        super.finishEditing(contactPoint);

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
                commitChanges();
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
            }
        }
    }

    /**
     * Modify height of terrain points.
     *
     * @param contactPoint the contact point.
     */
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final LocalObjects local = LocalObjects.get();
        final Node terrainNode = (Node) requireNonNull(getEditedModel());
        final Geometry levelMarker = getLevelMarker();

        final Vector3f markerTranslation = levelMarker.getLocalTranslation();
        final Vector3f worldTranslation = terrainNode.getWorldTranslation();
        final Vector3f localScale = terrainNode.getLocalScale();
        final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
        final Vector2f terrainLoc = local.nextVector2f();
        final Vector2f effectPoint = local.nextVector2f();

        final Terrain terrain = (Terrain) terrainNode;
        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        final float markerHeight = markerTranslation.getY() - worldTranslation.getY();
        final float desiredHeight = isUseMarker() ? markerHeight : getLevel();

        final int radiusStepsX = (int) (brushSize / localScale.getX());
        final int radiusStepsZ = (int) (brushSize / localScale.getZ());

        final float xStepAmount = localScale.getX();
        final float zStepAmount = localScale.getZ();

        final List<Vector2f> locs = new ArrayList<>();
        final List<Float> heights = new ArrayList<>();

        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                float locX = localPoint.getX() + (x * xStepAmount);
                float locZ = localPoint.getZ() + (z * zStepAmount);

                effectPoint.set(locX - localPoint.getX(), locZ - localPoint.getZ());

                if (!isContains(brush, effectPoint.getX(), effectPoint.getY())) {
                    continue;
                }

                terrainLoc.set(locX, locZ);

                // adjust height based on radius of the tool
                final float currentHeight = terrain.getHeightmapHeight(terrainLoc) * localScale.getY();

                if (isPrecision()) {
                    locs.add(terrainLoc.clone());
                    heights.add(desiredHeight / localScale.getY());
                } else {

                    float epsilon = 0.0001f * brushPower; // rounding error for snapping
                    float adj = 0;

                    if (currentHeight < desiredHeight) adj = 1;
                    else if (currentHeight > desiredHeight) adj = -1;

                    adj *= brushPower;
                    adj *= calculateRadiusPercent(brushSize, effectPoint.getX(), effectPoint.getY());

                    // test if adjusting too far and then cap it
                    if (adj > 0 && floatGreaterThan((currentHeight + adj), desiredHeight, epsilon)) {
                        adj = desiredHeight - currentHeight;
                    } else if (adj < 0 && floatLessThan((currentHeight + adj), desiredHeight, epsilon)) {
                        adj = desiredHeight - currentHeight;
                    }

                    if (!floatEquals(adj, 0, 0.001f)) {
                        locs.add(terrainLoc.clone());
                        heights.add(currentHeight + adj);
                    }
                }
            }
        }

        locs.forEach(this::change);

        // do the actual height adjustment
        terrain.setHeight(locs, heights);

        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }

    /**
     * Sets level.
     *
     * @param level the target level.
     */
    public void setLevel(final float level) {
        this.level = level;
    }

    /**
     * Gets level.
     *
     * @return the target level.
     */
    public float getLevel() {
        return level;
    }

    /**
     * Is precision boolean.
     *
     * @return true if using precision changing.
     */
    public boolean isPrecision() {
        return precision;
    }

    /**
     * Sets precision.
     *
     * @param precision the flag of using precision changing.
     */
    public void setPrecision(final boolean precision) {
        this.precision = precision;
    }

    /**
     * Sets use marker.
     *
     * @param useMarker the flag of using marker.
     */
    public void setUseMarker(final boolean useMarker) {
        this.useMarker = useMarker;
    }

    /**
     * Is use marker boolean.
     *
     * @return the flag of using marker.
     */
    public boolean isUseMarker() {
        return useMarker;
    }

    /**
     * @return the level marker.
     */
    @NotNull
    private Geometry getLevelMarker() {
        return levelMarker;
    }
}
