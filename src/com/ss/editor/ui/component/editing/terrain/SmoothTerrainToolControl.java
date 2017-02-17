package com.ss.editor.ui.component.editing.terrain;

import static com.ss.editor.util.EditingUtils.isContains;
import static java.util.Objects.requireNonNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.ss.editor.control.editing.EditingInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of terrain tool to smooth heights.
 *
 * @author JavaSaBr
 */
public class SmoothTerrainToolControl extends ChangeHeightTerrainToolControl {

    public SmoothTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
    }

    @NotNull
    @Override
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Yellow;
    }

    @Override
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        super.startEditing(editingInput, contactPoint);

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                startChange();
                modifyHeight(contactPoint);
            }
        }
    }

    @Override
    public void updateEditing(@NotNull final Vector3f contactPoint) {

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
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
            }
        }
    }

    /**
     * Modify height of terrain points.
     *
     * @param contactPoint the contact point.
     */
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final Node terrainNode = (Node) requireNonNull(getEditedModel());
        final Terrain terrain = (Terrain) terrainNode;
        final Vector3f localScale = terrainNode.getLocalScale();

        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        final int radiusStepsX = (int) (brushSize / localScale.getX());
        final int radiusStepsZ = (int) (brushSize / localScale.getZ());

        final float xStepAmount = localScale.getX();
        final float zStepAmount = localScale.getZ();

        final List<Vector2f> locs = new ArrayList<>();
        final List<Float> heights = new ArrayList<>();

        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                float locX = contactPoint.getX() + (x * xStepAmount);
                float locZ = contactPoint.getZ() + (z * zStepAmount);

                if (!isContains(brush, locX - contactPoint.getX(), locZ - contactPoint.getZ())) {
                    continue;
                }

                final Vector2f terrainLoc = new Vector2f(locX, locZ);

                // adjust height based on radius of the tool
                final float center = terrain.getHeightmapHeight(terrainLoc);
                final float left = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x - 1, terrainLoc.y));
                final float right = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x + 1, terrainLoc.y));
                final float up = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x, terrainLoc.y + 1));
                final float down = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x, terrainLoc.y - 1));

                int count = 1;

                float amount = center;

                if (!isNaN(left)) {
                    amount += left;
                    count++;
                }
                if (!isNaN(right)) {
                    amount += right;
                    count++;
                }
                if (!isNaN(up)) {
                    amount += up;
                    count++;
                }
                if (!isNaN(down)) {
                    amount += down;
                    count++;
                }

                amount /= count; // take average

                // weigh it
                float diff = amount - center;
                diff *= brushPower;

                locs.add(terrainLoc);
                heights.add(diff);
            }
        }

        locs.forEach(this::change);

        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);
        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }

    private boolean isNaN(float val) {
        return val != val;
    }
}
