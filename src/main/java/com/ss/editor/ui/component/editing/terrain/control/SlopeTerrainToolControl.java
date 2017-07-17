package com.ss.editor.ui.component.editing.terrain.control;

import static com.ss.editor.util.EditingUtils.*;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
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
import com.ss.editor.control.editing.EditingInput;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
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

    /**
     * Instantiates a new Slope terrain tool control.
     *
     * @param component the component
     */
    public SlopeTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);

        this.baseMarker = new Geometry("BaseMarker", new Sphere(8, 8, 1));
        this.baseMarker.setMaterial(createMaterial(ColorRGBA.Red));
        this.targetMarker = new Geometry("TargetMarker", new Sphere(8, 8, 1));
        this.targetMarker.setMaterial(createMaterial(ColorRGBA.Blue));
        this.line = new Geometry("line", new Line(Vector3f.ZERO, Vector3f.ZERO));
        this.line.setMaterial(createMaterial(ColorRGBA.White));
    }

    @NotNull
    @Override
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.White;
    }

    @Override
    protected void onAttached(@NotNull final Node node) {
        super.onAttached(node);

        final Spatial editedModel = requireNonNull(getEditedModel());
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
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        super.startEditing(editingInput, contactPoint);

        switch (editingInput) {
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
    public void updateEditing(@NotNull final Vector3f contactPoint) {

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
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
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        super.finishEditing(contactPoint);

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
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
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final LocalObjects local = LocalObjects.get();
        final Node terrainNode = (Node) requireNonNull(getEditedModel());
        final Geometry baseMarker = getBaseMarker();
        final Geometry targetMarker = getTargetMarker();

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

        final Terrain terrain = (Terrain) terrainNode;

        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final float brushPower = getBrushPower();

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

        locs.forEach(this::change);

        // do the actual height adjustment
        terrain.setHeight(locs, heights);
        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }

    /**
     * @return the line between markers.
     */
    @NotNull
    private Geometry getLine() {
        return line;
    }

    /**
     * @return the base marker.
     */
    @NotNull
    private Geometry getBaseMarker() {
        return baseMarker;
    }

    /**
     * @return the target marker.
     */
    @NotNull
    private Geometry getTargetMarker() {
        return targetMarker;
    }

    /**
     * Is precision boolean.
     *
     * @return true if using precision changing.
     */
    public boolean isPrecision() {
        return precision;
    }

    /**
     * Sets precision.
     *
     * @param precision the flag of using precision changing.
     */
    public void setPrecision(final boolean precision) {
        this.precision = precision;
    }

    /**
     * @return true if can edit only between markers.
     */
    private boolean isLock() {
        return lock;
    }

    /**
     * Sets lock.
     *
     * @param lock the flag of locking.
     */
    public void setLock(final boolean lock) {
        this.lock = lock;
    }
}
