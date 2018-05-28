package com.ss.editor.control.painting.terrain;

import static com.ss.editor.util.PaintingUtils.isContains;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.lang.Math.max;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import com.ss.editor.util.PaintingUtils;
import com.ss.rlib.common.util.ExtMath;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The implementation of terrain tool to make slopes.
 *
 * @author JavaSaBr
 */
public class SlopeTerrainToolControl extends ChangeHeightTerrainToolControl {

    /**
     * The base marker.
     */
    @NotNull
    private final Geometry baseMarker;

    /**
     * The target marker.
     */
    @NotNull
    private final Geometry targetMarker;

    /**
     * The line between markers.
     */
    @NotNull
    private final Geometry line;

    /**
     * The flag of using precision changing.
     */
    private boolean precision;

    /**
     * The flag of locking.
     */
    private boolean lock;

    public SlopeTerrainToolControl(@NotNull TerrainPaintingComponent component) {
        super(component);

        this.baseMarker = new Geometry("BaseMarker", new Sphere(8, 8, 1));
        this.baseMarker.setMaterial(createColoredMaterial(ColorRGBA.Red));
        this.targetMarker = new Geometry("TargetMarker", new Sphere(8, 8, 1));
        this.targetMarker.setMaterial(createColoredMaterial(ColorRGBA.Blue));
        this.line = new Geometry("line", new Line(Vector3f.ZERO, Vector3f.ZERO));
        this.line.setMaterial(createColoredMaterial(ColorRGBA.White));
    }

    @Override
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.White;
    }

    @Override
    @JmeThread
    protected void onAttached(@NotNull Node node) {
        super.onAttached(node);

        var editedModel = notNull(getPaintedModel());
        var baseMarker = getBaseMarker();
        var targetMarker = getTargetMarker();
        var line = getLine();

        var markersNode = component.getMarkersNode();
        markersNode.attachChild(baseMarker);
        markersNode.attachChild(targetMarker);
        markersNode.attachChild(line);

        baseMarker.setLocalTranslation(editedModel.getWorldTranslation());
        targetMarker.setLocalTranslation(editedModel.getWorldTranslation());
    }

    @Override
    @JmeThread
    protected void onDetached(@NotNull Node node) {
        super.onDetached(node);

        var baseMarker = getBaseMarker();
        var targetMarker = getTargetMarker();
        var line = getLine();

        var markersNode = component.getMarkersNode();
        markersNode.detachChild(baseMarker);
        markersNode.detachChild(targetMarker);
        markersNode.detachChild(line);
    }

    @Override
    @JmeThread
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);

        var baseMarker = getBaseMarker();
        var targetMarker = getTargetMarker();
        var line = getLine();

        var firstPoint = baseMarker.getLocalTranslation();
        var secondPoint = targetMarker.getLocalTranslation();

        var mesh = (Line) line.getMesh();
        mesh.updatePoints(firstPoint, secondPoint);
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
                baseMarker.setLocalTranslation(contactPoint);
                break;
            }
            case MOUSE_SECONDARY_WITH_CTRL: {
                targetMarker.setLocalTranslation(contactPoint);
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
                baseMarker.setLocalTranslation(contactPoint);
                break;
            }
            case MOUSE_SECONDARY_WITH_CTRL: {
                targetMarker.setLocalTranslation(contactPoint);
                break;
            }
        }
    }

    @Override
    @JmeThread
    public void finishPainting(@NotNull Quaternion brushRotation, @NotNull Vector3f contactPoint) {
        super.finishPainting(brushRotation, contactPoint);

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
                commitChanges();
                break;
            }
            case MOUSE_SECONDARY: {
                baseMarker.setLocalTranslation(contactPoint);
                break;
            }
            case MOUSE_SECONDARY_WITH_CTRL: {
                targetMarker.setLocalTranslation(contactPoint);
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
        var baseMarker = getBaseMarker();
        var targetMarker = getTargetMarker();

        var brushSize = getBrushSize();
        var brushPower = getBrushPower();

        var locs = new ArrayList<Vector2f>();
        var heights = new ArrayList<Float>();

        for (var terrain : getTerrains()) {

            var terrainNode = (Node) terrain;

            locs.clear();
            heights.clear();

            var worldTranslation = terrainNode.getWorldTranslation();
            var localScale = terrainNode.getLocalScale();
            var firstPoint = baseMarker.getLocalTranslation();
            var secondPoint = targetMarker.getLocalTranslation();
            var localPoint = contactPoint.subtract(worldTranslation, local.nextVector());

            Vector3f higher, lower;

            // Make sure we go for the right direction, or we could be creating a slope to the oposite side
            if (firstPoint.getY() > secondPoint.getY()) {
                higher = firstPoint.subtract(worldTranslation, local.nextVector());
                lower = secondPoint.subtract(worldTranslation, local.nextVector());
            } else {
                higher = secondPoint.subtract(worldTranslation, local.nextVector());
                lower = firstPoint.subtract(worldTranslation, local.nextVector());
            }

            var subtract = higher.subtract(lower, local.nextVector());
            var normal = lower.subtract(higher, local.nextVector()).normalize();
            var firstSide = local.nextVector();
            var secondSide = local.nextVector();
            var targetPoint = local.nextVector();
            var terrainLoc = local.nextVector2f();
            var effectPoint = local.nextVector2f();

            var radiusStepsX = (int) (brushSize / localScale.getX());
            var radiusStepsZ = (int) (brushSize / localScale.getY());

            var xStepAmount = localScale.getX();
            var zStepAmount = localScale.getZ();

            var firstPlane = local.nextPlane();
            firstPlane.setOriginNormal(lower, normal);

            var secondPlane = local.nextPlane();
            secondPlane.setOriginNormal(higher, normal);

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
                    var heightmapHeight = terrain.getHeightmapHeight(terrainLoc);
                    if (Float.isNaN(heightmapHeight)) {
                        continue;
                    }

                    var currentHeight = heightmapHeight * localScale.getY();

                    targetPoint.set(locX, currentHeight, locZ)
                            .subtractLocal(lower)
                            .projectLocal(subtract)
                            .addLocal(lower);

                    var lowerDist = lower.distance(targetPoint);
                    var higherDist = higher.distance(targetPoint);
                    var maxDistance = lower.distance(higher);

                    float distance;

                    if(lowerDist < higherDist && higherDist > maxDistance) {
                        distance = 0F;
                    } else {
                        distance = lower.distance(targetPoint) / max(lower.distance(higher), 0.00001F);
                    }

                    var desiredHeight = lower.getY() + (higher.getY() - lower.getY()) * distance;

                    firstSide.set(locX, 0f, locZ);
                    secondSide.set(locX, 0f, locZ);

                    if (isLock() && firstPlane.whichSide(firstSide) == secondPlane.whichSide(secondSide)) {
                        continue;
                    }

                    if (!isPrecision()) {

                        // rounding error for snapping
                        var epsilon = 0.0001f * brushPower;
                        var adj = 0F;

                        if (currentHeight < desiredHeight) {
                            adj = 1F;
                        } else if (currentHeight > desiredHeight) {
                            adj = -1F;
                        }

                        adj *= brushPower;
                        adj *= PaintingUtils.calculateRadiusPercent(brushSize, effectPoint.getX(), effectPoint.getY());

                        // test if adjusting too far and then cap it
                        if ((adj > 0) && ExtMath.greaterThan((currentHeight + adj), desiredHeight, epsilon)) {
                            adj = desiredHeight - currentHeight;
                        } else if (adj < 0 && ExtMath.lessThan((currentHeight + adj), desiredHeight, epsilon)) {
                            adj = desiredHeight - currentHeight;
                        }

                        if (!ExtMath.equals(adj, 0, 0.001f)) {
                            locs.add(terrainLoc.clone());
                            heights.add(currentHeight + adj);
                        }

                    } else {
                        locs.add(terrainLoc.clone());
                        heights.add(desiredHeight / localScale.getY());
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
     * Get the line between markers.
     *
     * @return the line between markers.
     */
    @JmeThread
    private @NotNull Geometry getLine() {
        return line;
    }

    /**
     * Get the base marker.
     *
     * @return the base marker.
     */
    @JmeThread
    private @NotNull Geometry getBaseMarker() {
        return baseMarker;
    }

    /**
     * Get the target marker.
     *
     * @return the target marker.
     */
    @JmeThread
    private @NotNull Geometry getTargetMarker() {
        return targetMarker;
    }

    /**
     * Return the flag of using precision changing.
     *
     * @return true if using precision changing.
     */
    @JmeThread
    public boolean isPrecision() {
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
     * Return true if can edit only between markers.
     *
     * @return true if can edit only between markers.
     */
    @JmeThread
    private boolean isLock() {
        return lock;
    }

    /**
     * Set the the flag of locking.
     *
     * @param lock the flag of locking.
     */
    @JmeThread
    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
