package com.ss.builder.util;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.jme.control.painting.PaintingControl;
import com.ss.rlib.common.geom.util.CoordsUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The utility class.
 *
 * @author JavaSaBr
 */
public class PaintingUtils {

    /**
     * Get a painting control of the cursor node.
     *
     * @param cursorNode the cursor node.
     * @return the painting control or null.
     */
    @FromAnyThread
    public static @Nullable PaintingControl getPaintingControl(@NotNull Node cursorNode) {
        return cursorNode.getControl(PaintingControl.class);
    }

    /**
     * Get an edit model of the cursor node.
     *
     * @param cursorNode the cursor node.
     * @return the edited model or null.
     */
    @FromAnyThread
    public static @Nullable Spatial getPaintedModel(@NotNull Node cursorNode) {
        var control = getPaintingControl(cursorNode);
        var paintedModel = control == null ? null : control.getPaintedModel();
        return paintedModel instanceof Spatial ? (Spatial) paintedModel : null;
    }

    /**
     * Get a painted model.
     *
     * @param control the painting control.
     * @return the painted model or null.
     */
    @FromAnyThread
    public static @Nullable Spatial getPaintedModel(@Nullable PaintingControl control) {
        var paintedModel = control == null ? null : control.getPaintedModel();
        return paintedModel instanceof Spatial ? (Spatial) paintedModel : null;
    }

    /**
     * Check that the point is contains in the geometry.
     *
     * @param geometry the geometry.
     * @param x        the X component.
     * @param y        the Y component.
     * @return true if the geometry contains the point.
     */
    @FromAnyThread
    public static boolean isContains(@NotNull Geometry geometry, float x, float y) {

        var mesh = geometry.getMesh();
        var localScale = geometry.getLocalScale();

        if (mesh instanceof Sphere) {
            var radius = ((Sphere) mesh).getRadius() * localScale.getX();
            // return true if the distance is less than equal to the radius
            return Math.abs(CoordsUtils.length(x, y)) <= radius;
        }

        return false;
    }

    /**
     * Interpolate the height value based on its distance from the center (how far along
     * the radius it is).
     * The farther from the center, the less the height will be.
     * This produces a linear height falloff.
     *
     * @param radius       of the tool.
     * @param heightFactor potential height value to be adjusted.
     * @param x            the X component.
     * @param y            the Y component.
     * @return the adjusted height value.
     */
    @FromAnyThread
    public static float calculateHeight(float radius, float heightFactor, float x, float y) {
        return heightFactor * calculateRadiusPercent(radius, x, y);
    }

    /**
     * Calculate percent of vector length and radius.
     *
     * @param radius the radius.
     * @param x      the X component.
     * @param y      the Y component.
     * @return the result percent.
     */
    @FromAnyThread
    public static float calculateRadiusPercent(float radius, float x, float y) {
        // find percentage for each 'unit' in radius
        var val = Math.abs(CoordsUtils.length(x, y)) / radius;
        val = 1f - val;
        return val;
    }
}
