package com.ss.editor.ui.component.editing.terrain;

import static java.util.Objects.requireNonNull;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.ss.editor.control.editing.EditingInput;
import com.ss.editor.util.EditingUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The raise implementation of terrain tool.
 *
 * @author JavaSaBr
 */
public class RaiseLowerTerrainToolControl extends ChangeHeightTerrainToolControl {

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

        final Node terrainNode = (Node) requireNonNull(getEditedModel());
        final Terrain terrain = (Terrain) terrainNode;
        final Vector3f worldScale = terrainNode.getWorldScale();

        final Geometry brush = getBrush();
        brush.updateModelBound();

        final BoundingVolume worldBound = brush.getWorldBound();

        final float brushSize = getBrushSize();
        final float brushPower = editingInput == EditingInput.MOUSE_PRIMARY ? getBrushPower() : getBrushPower() * -1F;

        final int radiusStepsX = (int) (brushSize / worldScale.getX());
        final int radiusStepsZ = (int) (brushSize / worldScale.getY());

        final float xStepAmount = worldScale.getX();
        final float zStepAmount = worldScale.getZ();

        final Vector3f point = new Vector3f(contactPoint);

        final List<Vector2f> locs = new ArrayList<>();
        final List<Float> heights = new ArrayList<>();

        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                float locX = contactPoint.getX() + (x * xStepAmount);
                float locZ = contactPoint.getZ() + (z * zStepAmount);

                point.setX(locX);
                point.setZ(locZ);

                if (!worldBound.contains(point)) {
                    continue;
                }

                point.setX(locX - contactPoint.getX());
                point.setZ(locZ - contactPoint.getZ());

                // adjust height based on radius of the tool
                float newHeight = EditingUtils.calculateHeight(brushSize, brushPower, point.getX(), point.getZ());
                // increase the height
                locs.add(new Vector2f(locX, locZ));
                heights.add(newHeight);
            }
        }

        locs.forEach(this::change);

        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);
        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }
}
