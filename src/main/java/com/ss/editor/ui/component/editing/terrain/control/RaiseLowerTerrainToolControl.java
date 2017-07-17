package com.ss.editor.ui.component.editing.terrain.control;

import static com.ss.editor.util.EditingUtils.calculateHeight;
import static com.ss.editor.util.EditingUtils.isContains;
import static java.util.Objects.requireNonNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.ss.editor.control.editing.EditingInput;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of terrain tool to raise/lowe heights.
 *
 * @author JavaSaBr
 */
public class RaiseLowerTerrainToolControl extends ChangeHeightTerrainToolControl {

    /**
     * Instantiates a new Raise lower terrain tool control.
     *
     * @param component the component
     */
    public RaiseLowerTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
    }

    @NotNull
    @Override
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Green;
    }

    @Override
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        super.startEditing(editingInput, contactPoint);

        switch (editingInput) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                startChange();
                modifyHeight(editingInput, contactPoint);
            }
        }
    }

    @Override
    public void updateEditing(@NotNull final Vector3f contactPoint) {

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                modifyHeight(editingInput, contactPoint);
            }
        }
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        super.finishEditing(contactPoint);

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                modifyHeight(editingInput, contactPoint);
                commitChanges();
            }
        }
    }

    /**
     * Modify height of terrain points.
     *
     * @param editingInput the type of input.
     * @param contactPoint the contact point.
     */
    private void modifyHeight(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {

        final LocalObjects local = LocalObjects.get();
        final Node terrainNode = (Node) requireNonNull(getEditedModel());

        final Vector3f worldTranslation = terrainNode.getWorldTranslation();
        final Vector3f localScale = terrainNode.getLocalScale();
        final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
        final Vector2f terrainLoc = local.nextVector2f();
        final Vector2f effectPoint = local.nextVector2f();

        final Terrain terrain = (Terrain) terrainNode;
        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = editingInput == EditingInput.MOUSE_PRIMARY ? getBrushPower() : getBrushPower() * -1F;

        final int radiusStepsX = (int) (brushSize / localScale.getX());
        final int radiusStepsZ = (int) (brushSize / localScale.getY());

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

                final float currentHeight = terrain.getHeightmapHeight(terrainLoc) * localScale.getY();
                // adjust height based on radius of the tool
                final float newHeight = calculateHeight(brushSize, brushPower, effectPoint.getX(), effectPoint.getY());

                // increase the height
                locs.add(terrainLoc.clone());
                heights.add(currentHeight + newHeight);
            }
        }

        locs.forEach(this::change);

        // do the actual height adjustment
        terrain.setHeight(locs, heights);
        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }
}
