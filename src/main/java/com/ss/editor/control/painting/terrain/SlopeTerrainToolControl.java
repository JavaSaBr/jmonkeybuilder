package com.ss.editor.control.painting.terrain;

import static com.ss.editor.util.PaintingUtils.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Math.max;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
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

    public SlopeTerrainToolControl(@NotNull final TerrainPaintingComponent component) {
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
    protected void onAttached(@NotNull final Node node) {
        super.onAttached(node);

        final Spatial editedModel = notNull(getPaintedModel());
        final Geometry baseMarker = getBaseMarker();
        final Geometry targetMarker = getTargetMarker();
        final Geometry line = getLine();

        final Node markersNode = component.getMarkersNode();
        markersNode.attachChild(baseMarker);
        markersNode.attachChild(targetMarker);
        markersNode.attachChild(line);

        baseMarker.setLocalTranslation(editedModel.getWorldTranslation());
        targetMarker.setLocalTranslation(editedModel.getWorldTranslation());
    }

    @Override
    @JmeThread
    protected void onDetached(@NotNull final Node node) {
        super.onDetached(node);

        final Geometry baseMarker = getBaseMarker();
        final Geometry targetMarker = getTargetMarker();
        final Geometry line = getLine();

        final Node markersNode = component.getMarkersNode();
        markersNode.detachChild(baseMarker);
        markersNode.detachChild(targetMarker);
        markersNode.detachChild(line);
    }

    @Override
    @JmeThread
    protected void controlUpdate(final float tpf) {
        super.controlUpdate(tpf);

        final Geometry baseMarker = getBaseMarker();
        final Geometry targetMarker = getTargetMarker();
        final Geometry line = getLine();

        final Vector3f firstPoint = baseMarker.getLocalTranslation();
        final Vector3f secondPoint = targetMarker.getLocalTranslation();

        final Line mesh = (Line) line.getMesh();
        mesh.updatePoints(firstPoint, secondPoint);
    }

    @Override
    @JmeThread
    public void startPainting(@NotNull final PaintingInput input, @NotNull final Vector3f contactPoint) {
        super.startPainting(input, contactPoint);

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
    public void updatePainting(@NotNull final Vector3f contactPoint) {

        final PaintingInput input = notNull(getCurrentInput());

        switch (input) {
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
    public void finishPainting(@NotNull final Vector3f contactPoint) {
        super.finishPainting(contactPoint);

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
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final LocalObjects local = getLocalObjects();
        final Spatial paintedModel = notNull(getPaintedModel());

        final Geometry brush = getBrush();
        final Geometry baseMarker = getBaseMarker();
        final Geometry targetMarker = getTargetMarker();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

        for (final Terrain terrain : getTerrains()) {

            final Node terrainNode = (Node) terrain;

            final Vector3f worldTranslation = terrainNode.getWorldTranslation();
            final Vector3f localScale = terrainNode.getLocalScale();
            final Vector3f firstPoint = baseMarker.getLocalTranslation();
            final Vector3f secondPoint = targetMarker.getLocalTranslation();
            final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());

            Vector3f higher, lower;

            // Make sure we go for the right direction, or we could be creating a slope to the oposite side
            if (firstPoint.getY() > secondPoint.getY()) {
                higher = firstPoint.subtract(worldTranslation, local.nextVector());
                lower = secondPoint.subtract(worldTranslation, local.nextVector());
            } else {
                higher = secondPoint.subtract(worldTranslation, local.nextVector());
                lower = firstPoint.subtract(worldTranslation, local.nextVector());
            }

            final Vector3f subtract = higher.subtract(lower, local.nextVector());
            final Vector3f normal = lower.subtract(higher, local.nextVector()).normalize();
            final Vector3f firstSide = local.nextVector();
            final Vector3f secondSide = local.nextVector();
            final Vector3f targetPoint = local.nextVector();
            final Vector2f terrainLoc = local.nextVector2f();
            final Vector2f effectPoint = local.nextVector2f();

            final int radiusStepsX = (int) (brushSize / localScale.getX());
            final int radiusStepsZ = (int) (brushSize / localScale.getY());

            final float xStepAmount = localScale.getX();
            final float zStepAmount = localScale.getZ();

            final Plane firstPlane = local.nextPlane();
            firstPlane.setOriginNormal(lower, normal);

            final Plane secondPlane = local.nextPlane();
            secondPlane.setOriginNormal(higher, normal);

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
                    float currentHeight = terrain.getHeightmapHeight(terrainLoc) * localScale.getY();

                    targetPoint.set(locX, currentHeight, locZ)
                            .subtractLocal(lower)
                            .projectLocal(subtract)
                            .addLocal(lower);

                    final float lowerDist = lower.distance(targetPoint);
                    final float higherDist = higher.distance(targetPoint);
                    final float maxDistance = lower.distance(higher);

                    float distance;

                    if(lowerDist < higherDist && higherDist > maxDistance) {
                        distance = 0F;
                    } else {
                        distance = lower.distance(targetPoint) / max(lower.distance(higher), 0.00001F);
                    }

                    final float desiredHeight = lower.getY() + (higher.getY() - lower.getY()) * distance;

                    firstSide.set(locX, 0f, locZ);
                    secondSide.set(locX, 0f, locZ);

                    if (isLock() && firstPlane.whichSide(firstSide) == secondPlane.whichSide(secondSide)) {
                        continue;
                    }

                    if (!isPrecision()) {

                        // rounding error for snapping
                        float epsilon = 0.0001f * brushPower;
                        float adj = 0;

                        if (currentHeight < desiredHeight) adj = 1;
                        else if (currentHeight > desiredHeight) adj = -1;

                        adj *= brushPower;
                        adj *= calculateRadiusPercent(brushSize, effectPoint.getX(), effectPoint.getY());

                        // test if adjusting too far and then cap it
                        if ((adj > 0) && floatGreaterThan((currentHeight + adj), desiredHeight, epsilon)) {
                            adj = desiredHeight - currentHeight;
                        } else if (adj < 0 && floatLessThan((currentHeight + adj), desiredHeight, epsilon)) {
                            adj = desiredHeight - currentHeight;
                        }

                        if (!floatEquals(adj, 0, 0.001f)) {
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
    public void setPrecision(final boolean precision) {
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
    public void setLock(final boolean lock) {
        this.lock = lock;
    }
}
