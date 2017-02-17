package com.ss.editor.ui.component.editing.terrain;

import static com.ss.editor.util.EditingUtils.*;
import static java.util.Objects.requireNonNull;
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
 * The implementation of terrain tool to change height by level.
 *
 * @author JavaSaBr
 */
public class LevelTerrainToolControl extends ChangeHeightTerrainToolControl {

    private boolean precision;

    public LevelTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
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
        final Vector3f worldScale = terrainNode.getWorldScale();
        final Vector3f localScale = terrainNode.getLocalScale();

        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        final float desiredHeight = 3;

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
                final float currentHeight = terrain.getHeightmapHeight(terrainLoc) * worldScale.getY();

                if (precision) {
                    locs.add(terrainLoc);
                    heights.add(desiredHeight / localScale.getY());
                } else {

                    float epsilon = 0.1f * brushPower; // rounding error for snapping
                    float adj = 0;

                    if (currentHeight < desiredHeight) {
                        adj = 1;
                    } else if (currentHeight > desiredHeight) {
                        adj = -1;
                    }

                    adj *= brushPower;
                    adj *= EditingUtils.calculateRadiusPercent(brushSize, locX - contactPoint.x, locZ - contactPoint.z);

                    // test if adjusting too far and then cap it
                    if (adj > 0 && floatGreaterThan((currentHeight + adj), desiredHeight, epsilon)) {
                        adj = desiredHeight - currentHeight;
                    } else if (adj < 0 && floatLessThan((currentHeight + adj), desiredHeight, epsilon)) {
                        adj = currentHeight - desiredHeight;
                    }

                    if (!floatEquals(adj, 0, 0.001f)) {
                        locs.add(terrainLoc);
                        heights.add(adj);
                    }
                }
            }
        }

        locs.forEach(this::change);

        // do the actual height adjustment
        if (precision) {
            terrain.setHeight(locs, heights);
        } else {
            terrain.adjustHeight(locs, heights);
        }

        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }
}
