package com.ss.editor.util;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.control.painting.PaintingControl;
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
    public static @Nullable PaintingControl getPaintingControl(@NotNull final Node cursorNode) {
        return cursorNode.getControl(PaintingControl.class);
    }

    /**
     * Get an edit model of the cursor node.
     *
     * @param cursorNode the cursor node.
     * @return the edited model or null.
     */
    @FromAnyThread
    public static @Nullable Spatial getPaintedModel(@NotNull final Node cursorNode) {
        final PaintingControl control = getPaintingControl(cursorNode);
        return control == null ? null : control.getPaintedModel();
    }

    /**
     * Get a painted model.
     *
     * @param control the painting control.
     * @return the painted model or null.
     */
    @FromAnyThread
    public static @Nullable Spatial getPaintedModel(@Nullable final PaintingControl control) {
        return control == null ? null : control.getPaintedModel();
    }

    /**
     * Check that the point is contains in a geometry.
     *
     * @param geometry the geometry.
     * @param x        the X coord.
     * @param y        the Y coord.
     * @return true of the mesh contains the point.
     */
    @FromAnyThread
    public static boolean isContains(@NotNull final Geometry geometry, final float x, final float y) {

        final Mesh mesh = geometry.getMesh();
        final Vector3f localScale = geometry.getLocalScale();

        if (mesh instanceof Sphere) {
            final float radius = ((Sphere) mesh).getRadius() * localScale.getX();
            final Vector2f point = new Vector2f(x, y);
            // return true if the distance is less than equal to the radius
            return Math.abs(point.length()) <= radius;
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
     * @param x            location.
     * @param z            location.
     * @return the adjusted height value.
     */
    @FromAnyThread
    public static float calculateHeight(final float radius, final float heightFactor, final float x, final float z) {
        float val = calculateRadiusPercent(radius, x, z);
        return heightFactor * val;
    }

    /**
     * Calculate radius percent float.
     *
     * @param radius the radius.
     * @param x      the x.
     * @param z      the z.
     * @return the float.
     */
    @FromAnyThread
    public static float calculateRadiusPercent(final float radius, final float x, final float z) {
        // find percentage for each 'unit' in radius
        final Vector2f point = LocalObjects.get().nextVector(x, z);
        float val = Math.abs(point.length()) / radius;
        val = 1f - val;
        return val;
    }

    /**
     * Compare the two float values by the epsilon.
     *
     * @param first   the first.
     * @param second  the second.
     * @param epsilon the epsilon.
     * @return true if the values are equals.
     */
    @FromAnyThread
    public static boolean floatEquals(final float first, final float second, final float epsilon) {
        return first == second || Math.abs(first - second) < epsilon;
    }

    /**
     * Compare the two float values by the epsilon.
     *
     * @param first   the first.
     * @param second  the second.
     * @param epsilon the epsilon.
     * @return true if the first value is less than the second value.
     */
    @FromAnyThread
    public static boolean floatLessThan(final float first, final float second, final float epsilon) {
        return second - first > epsilon;
    }

    /**
     * Compare the two float values by the epsilon.
     *
     * @param first   the first.
     * @param second  the second.
     * @param epsilon the epsilon.
     * @return the boolean
     * @return true if the first value is greater than the second value.
     */
    @FromAnyThread
    public static boolean floatGreaterThan(final float first, final float second, final float epsilon) {
        return first - second > epsilon;
    }
}
