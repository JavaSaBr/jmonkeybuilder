package com.ss.editor.control.painting.terrain;

import static com.ss.editor.util.PaintingUtils.calculateHeight;
import static com.ss.editor.util.PaintingUtils.isContains;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
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

    public RaiseLowerTerrainToolControl(@NotNull final TerrainPaintingComponent component) {
        super(component);
    }

    @Override
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Green;
    }

    @Override
    @JmeThread
    public void startPainting(@NotNull final PaintingInput input, @NotNull final Quaternion brushRotation,
                              @NotNull final Vector3f contactPoint) {
        super.startPainting(input, brushRotation, contactPoint);

        switch (input) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                startChange();
                modifyHeight(input, contactPoint);
            }
        }
    }

    @Override
    @JmeThread
    public void updatePainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint,
                               final float tpf) {

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                modifyHeight(input, contactPoint);
            }
        }
    }

    @Override
    @JmeThread
    public void finishPainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint) {
        super.finishPainting(brushRotation, contactPoint);

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
            case MOUSE_PRIMARY:
            case MOUSE_SECONDARY: {
                modifyHeight(input, contactPoint);
                commitChanges();
            }
        }
    }

    /**
     * Modify height of terrain points.
     *
     * @param input the type of input.
     * @param contactPoint the contact point.
     */
    @JmeThread
    private void modifyHeight(@NotNull final PaintingInput input, @NotNull final Vector3f contactPoint) {

        final LocalObjects local = getLocalObjects();
        final Spatial paintedModel = notNull(getPaintedModel());
        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = input == PaintingInput.MOUSE_PRIMARY ? getBrushPower() : getBrushPower() * -1F;

        final List<Vector2f> locs = new ArrayList<>();
        final List<Float> heights = new ArrayList<>();

        for (final Terrain terrain : getTerrains()) {

            final Node terrainNode = (Node) terrain;

            locs.clear();
            heights.clear();

            final Vector3f worldTranslation = terrainNode.getWorldTranslation();
            final Vector3f localScale = terrainNode.getLocalScale();
            final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
            final Vector2f terrainLoc = local.nextVector2f();
            final Vector2f effectPoint = local.nextVector2f();

            final int radiusStepsX = (int) (brushSize / localScale.getX());
            final int radiusStepsZ = (int) (brushSize / localScale.getY());

            final float xStepAmount = localScale.getX();
            final float zStepAmount = localScale.getZ();

            for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
                for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                    float locX = localPoint.getX() + (x * xStepAmount);
                    float locZ = localPoint.getZ() + (z * zStepAmount);

                    effectPoint.set(locX - localPoint.getX(), locZ - localPoint.getZ());

                    if (!isContains(brush, effectPoint.getX(), effectPoint.getY())) {
                        continue;
                    }

                    terrainLoc.set(locX, locZ);

                    final float heightmapHeight = terrain.getHeightmapHeight(terrainLoc);
                    if (Float.isNaN(heightmapHeight)) {
                        continue;
                    }

                    final float currentHeight = heightmapHeight * localScale.getY();
                    // adjust height based on radius of the tool
                    final float newHeight = calculateHeight(brushSize, brushPower, effectPoint.getX(), effectPoint.getY());

                    // increase the height
                    locs.add(terrainLoc.clone());
                    heights.add(currentHeight + newHeight);
                }
            }

            locs.forEach(location -> change(terrain, location));
            // do the actual height adjustment
            terrain.setHeight(locs, heights);
        }

        // or else we won't collide with it where we just edited
        paintedModel.updateModelBound();
    }
}
