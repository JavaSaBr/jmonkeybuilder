package com.ss.editor.control.painting.terrain;

import static com.ss.editor.util.PaintingUtils.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
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

    public LevelTerrainToolControl(@NotNull final TerrainPaintingComponent component) {
        super(component);

        this.levelMarker = new Geometry("LevelMarker", new Sphere(8, 8, 1));
        this.levelMarker.setMaterial(createColoredMaterial(getBrushColor()));
    }

    @Override
    @JmeThread
    protected void onAttached(@NotNull final Node node) {
        super.onAttached(node);

        final Spatial editedModel = notNull(getPaintedModel());
        final Geometry levelMarker = getLevelMarker();

        final Node markersNode = component.getMarkersNode();
        markersNode.attachChild(levelMarker);

        levelMarker.setLocalTranslation(editedModel.getWorldTranslation());
    }

    @Override
    @JmeThread
    protected void onDetached(@NotNull final Node node) {
        super.onDetached(node);

        final Node markersNode = component.getMarkersNode();
        markersNode.detachChild(getLevelMarker());
    }

    @Override
    @JmeThread
    protected void controlUpdate(final float tpf) {
        super.controlUpdate(tpf);

        final Geometry levelMarker = getLevelMarker();
        levelMarker.setCullHint(isUseMarker() ? Spatial.CullHint.Never : Spatial.CullHint.Always);
    }

    @Override
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Red;
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
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
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
                break;
            }
            case MOUSE_SECONDARY: {
                levelMarker.setLocalTranslation(contactPoint);
                break;
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
    @JmeThread
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final LocalObjects local = getLocalObjects();
        final Spatial paintedModel = notNull(getPaintedModel());
        final Geometry brush = getBrush();
        final Geometry levelMarker = getLevelMarker();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        final List<Vector2f> locs = new ArrayList<>();
        final List<Float> heights = new ArrayList<>();

        for (final Terrain terrain : getTerrains()) {

            final Node terrainNode = (Node) terrain;

            final Vector3f markerTranslation = levelMarker.getLocalTranslation();
            final Vector3f worldTranslation = terrainNode.getWorldTranslation();
            final Vector3f localScale = terrainNode.getLocalScale();
            final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
            final Vector2f terrainLoc = local.nextVector2f();
            final Vector2f effectPoint = local.nextVector2f();

            final float markerHeight = markerTranslation.getY() - worldTranslation.getY();
            final float desiredHeight = isUseMarker() ? markerHeight : getLevel();

            final int radiusStepsX = (int) (brushSize / localScale.getX());
            final int radiusStepsZ = (int) (brushSize / localScale.getZ());

            final float xStepAmount = localScale.getX();
            final float zStepAmount = localScale.getZ();

            locs.clear();
            heights.clear();

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

            locs.forEach(location -> change(terrain, location));

            // do the actual height adjustment
            terrain.setHeight(locs, heights);
        }

        // or else we won't collide with it where we just edited
        paintedModel.updateModelBound();
    }

    /**
     * Set the target level.
     *
     * @param level the target level.
     */
    @JmeThread
    public void setLevel(final float level) {
        this.level = level;
    }

    /**
     * Get the target level.
     *
     * @return the target level.
     */
    @JmeThread
    public float getLevel() {
        return level;
    }

    /**
     * Return true if using precision changing.
     *
     * @return true if using precision changing.
     */
    @JmeThread
    private boolean isPrecision() {
        return precision;
    }

    /**
     * Set the flag of using precision changing.
     *
     * @param precision the flag of using precision changing.
     */
    @JmeThread
    public void setPrecision(final boolean precision) {
        this.precision = precision;
    }

    /**
     * Set the flag of using marker.
     *
     * @param useMarker the flag of using marker.
     */
    @JmeThread
    public void setUseMarker(final boolean useMarker) {
        this.useMarker = useMarker;
    }

    /**
     * Return the flag of using marker.
     *
     * @return the flag of using marker.
     */
    @JmeThread
    private boolean isUseMarker() {
        return useMarker;
    }

    /**
     * Get the level marker.
     *
     * @return the level marker.
     */
    @JmeThread
    private @NotNull Geometry getLevelMarker() {
        return levelMarker;
    }
}
