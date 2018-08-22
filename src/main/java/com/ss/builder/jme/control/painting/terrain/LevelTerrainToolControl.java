package com.ss.builder.control.painting.terrain;

import static com.ss.editor.util.PaintingUtils.isContains;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Sphere;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import com.ss.editor.util.PaintingUtils;
import com.ss.rlib.common.util.ExtMath;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

    public LevelTerrainToolControl(@NotNull TerrainPaintingComponent component) {
        super(component);

        this.levelMarker = new Geometry("LevelMarker", new Sphere(8, 8, 1));
        this.levelMarker.setMaterial(createColoredMaterial(getBrushColor()));
    }

    @Override
    @JmeThread
    protected void onAttached(@NotNull Node node) {
        super.onAttached(node);

        var editedModel = notNull(getPaintedModel());
        var levelMarker = getLevelMarker();

        component.getMarkersNode()
                .attachChild(levelMarker);

        levelMarker.setLocalTranslation(editedModel.getWorldTranslation());
    }

    @Override
    @JmeThread
    protected void onDetached(@NotNull Node node) {
        super.onDetached(node);
        component.getMarkersNode()
                .detachChild(getLevelMarker());
    }

    @Override
    @JmeThread
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        getLevelMarker().setCullHint(isUseMarker() ? CullHint.Never : CullHint.Always);
    }

    @Override
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Red;
    }

    @Override
    @JmeThread
    public void startPainting(
            @NotNull PaintingInput input,
            @NotNull Quaternion brushRotation,
            @NotNull Vector3f contactPoint
    ) {
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
    public void updatePainting(@NotNull Quaternion brushRotation, @NotNull Vector3f contactPoint, float tpf) {
        super.updatePainting(brushRotation, contactPoint, tpf);

        switch (notNull(getCurrentInput())) {
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
    public void finishPainting(@NotNull Quaternion brushRotation, @NotNull Vector3f contactPoint) {
        super.finishPainting(brushRotation, contactPoint);

        switch (notNull(getCurrentInput())) {
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
    private void modifyHeight(@NotNull Vector3f contactPoint) {

        var local = getLocalObjects();
        var paintedModel = notNull(getPaintedModel());
        var brush = getBrush();
        var levelMarker = getLevelMarker();

        var brushSize = getBrushSize();
        var brushPower = getBrushPower();

        var locs = new ArrayList<Vector2f>();
        var heights = new ArrayList<Float>();

        for (var terrain : getTerrains()) {

            var terrainNode = (Node) terrain;

            var markerTranslation = levelMarker.getLocalTranslation();
            var worldTranslation = terrainNode.getWorldTranslation();
            var localScale = terrainNode.getLocalScale();
            var localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
            var terrainLoc = local.nextVector2f();
            var effectPoint = local.nextVector2f();

            var markerHeight = markerTranslation.getY() - worldTranslation.getY();
            var desiredHeight = isUseMarker() ? markerHeight : getLevel();

            var radiusStepsX = (int) (brushSize / localScale.getX());
            var radiusStepsZ = (int) (brushSize / localScale.getZ());

            var xStepAmount = localScale.getX();
            var zStepAmount = localScale.getZ();

            locs.clear();
            heights.clear();

            for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
                for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                    var locX = localPoint.getX() + (x * xStepAmount);
                    var locZ = localPoint.getZ() + (z * zStepAmount);

                    effectPoint.set(locX - localPoint.getX(), locZ - localPoint.getZ());

                    if (!isContains(brush, effectPoint.getX(), effectPoint.getY())) {
                        continue;
                    }

                    terrainLoc.set(locX, locZ);

                    // adjust height based on radius of the tool
                    var currentHeight = terrain.getHeightmapHeight(terrainLoc) * localScale.getY();

                    if (isPrecision()) {
                        locs.add(terrainLoc.clone());
                        heights.add(desiredHeight / localScale.getY());
                    } else {

                        var epsilon = 0.0001f * brushPower; // rounding error for snapping
                        var adj = 0F;

                        if (currentHeight < desiredHeight) {
                            adj = 1;
                        } else if (currentHeight > desiredHeight) {
                            adj = -1;
                        }

                        adj *= brushPower;
                        adj *= PaintingUtils.calculateRadiusPercent(brushSize, effectPoint.getX(), effectPoint.getY());

                        // test if adjusting too far and then cap it
                        if (adj > 0 && ExtMath.greaterThan((currentHeight + adj), desiredHeight, epsilon)) {
                            adj = desiredHeight - currentHeight;
                        } else if (adj < 0 && ExtMath.lessThan((currentHeight + adj), desiredHeight, epsilon)) {
                            adj = desiredHeight - currentHeight;
                        }

                        if (!ExtMath.equals(adj, 0, 0.001f)) {
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
    public void setLevel(float level) {
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
    public void setPrecision(boolean precision) {
        this.precision = precision;
    }

    /**
     * Set the flag of using marker.
     *
     * @param useMarker the flag of using marker.
     */
    @JmeThread
    public void setUseMarker(boolean useMarker) {
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
