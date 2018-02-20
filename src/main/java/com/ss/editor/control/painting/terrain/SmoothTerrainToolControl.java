package com.ss.editor.control.painting.terrain;

import static com.ss.editor.util.PaintingUtils.isContains;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Float.isNaN;
import static java.lang.Math.min;
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
 * The implementation of terrain tool to smooth heights.
 *
 * @author JavaSaBr
 */
public class SmoothTerrainToolControl extends ChangeHeightTerrainToolControl {

    public SmoothTerrainToolControl(@NotNull final TerrainPaintingComponent component) {
        super(component);
    }

    @Override
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Yellow;
    }

    @Override
    @JmeThread
    public void startPainting(@NotNull final PaintingInput input, @NotNull final Quaternion brushRotation,
                              @NotNull final Vector3f contactPoint) {
        super.startPainting(input, brushRotation, contactPoint);

        switch (input) {
            case MOUSE_PRIMARY: {
                startChange();
                modifyHeight(contactPoint);
            }
        }
    }

    @Override
    @JmeThread
    public void updatePainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint,
                               final float tpf) {

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
            }
        }
    }

    @Override
    @JmeThread
    public void finishPainting(@NotNull final Quaternion brushRotation, @NotNull final Vector3f contactPoint) {
        super.finishPainting(brushRotation, contactPoint);

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
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
    @JmeThread
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final LocalObjects local = getLocalObjects();
        final Spatial paintedModel = notNull(getPaintedModel());
        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        final List<Vector2f> locs = new ArrayList<>();

        for (final Terrain terrain : getTerrains()) {

            final Node terrainNode = (Node) terrain;

            final Vector3f localScale = terrainNode.getLocalScale();
            final Vector3f worldTranslation = terrainNode.getWorldTranslation();
            final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
            final Vector2f terrainLoc = local.nextVector2f();
            final Vector2f terrainLocLeft = local.nextVector2f();
            final Vector2f terrainLocRight = local.nextVector2f();
            final Vector2f terrainLocUp = local.nextVector2f();
            final Vector2f terrainLocDown = local.nextVector2f();
            final Vector2f effectPoint = local.nextVector2f();

            final int radiusStepsX = (int) (brushSize / localScale.getX());
            final int radiusStepsZ = (int) (brushSize / localScale.getZ());

            final float xStepAmount = localScale.getX();
            final float zStepAmount = localScale.getZ();

            locs.clear();

            for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
                for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                    float locX = localPoint.getX() + (x * xStepAmount);
                    float locZ = localPoint.getZ() + (z * zStepAmount);

                    effectPoint.set(locX - localPoint.getX(), locZ - localPoint.getZ());

                    if (!isContains(brush, effectPoint.getX(), effectPoint.getY())) {
                        continue;
                    }

                    terrainLoc.set(locX, locZ);
                    terrainLocLeft.set(terrainLoc.getX() - 1, terrainLoc.getY());
                    terrainLocRight.set(terrainLoc.getX() + 1, terrainLoc.getY());
                    terrainLocUp.set(terrainLoc.getX(), terrainLoc.getY() + 1);
                    terrainLocDown.set(terrainLoc.getX(), terrainLoc.getY() - 1);

                    // adjust height based on radius of the tool
                    final float center = terrain.getHeightmapHeight(terrainLoc);
                    final float left = terrain.getHeightmapHeight(terrainLocLeft);
                    final float right = terrain.getHeightmapHeight(terrainLocRight);
                    final float up = terrain.getHeightmapHeight(terrainLocUp);
                    final float down = terrain.getHeightmapHeight(terrainLocDown);

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

                    // take average
                    amount /= count;

                    // weigh it
                    float diff = amount - center;
                    diff *= min(brushPower, 2F);

                    terrain.setHeight(terrainLoc, center + diff);
                    locs.add(terrainLoc.clone());
                }
            }

            locs.forEach(location -> change(terrain, location));
        }

        // or else we won't collide with it where we just edited
        paintedModel.updateModelBound();
    }
}
