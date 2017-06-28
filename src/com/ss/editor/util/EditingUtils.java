package com.ss.editor.util;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.control.editing.EditingControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The utility class.
 *
 * @author JavaSaBr
 */
public class EditingUtils {

    /**
     * Get an editing control of a cursor node.
     *
     * @param cursorNode the cursor node.
     * @return the editing control or null.
     */
    @Nullable
    public static EditingControl getEditingControl(@NotNull final Node cursorNode) {
        return cursorNode.getControl(EditingControl.class);
    }

    /**
     * Get an edit model of a cursor node.
     *
     * @param cursorNode the cursor node.
     * @return the edited model or null.
     */
    @Nullable
    public static Spatial getEditedModel(@NotNull final Node cursorNode) {
        final EditingControl control = getEditingControl(cursorNode);
        return control == null ? null : control.getEditedModel();
    }

    /**
     * Get an edit model.
     *
     * @param control the editing control.
     * @return the edited model or null.
     */
    @Nullable
    public static Spatial getEditedModel(@Nullable final EditingControl control) {
        return control == null ? null : control.getEditedModel();
    }

    /**
     * Check that a point is contains in a geometry.
     *
     * @param geometry the geometry.
     * @param x        the X coord.
     * @param y        the Y coord.
     * @return true of the mesh contains the point.
     */
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
     * @param radius       of the tool
     * @param heightFactor potential height value to be adjusted
     * @param x            location
     * @param z            location
     * @return the adjusted height value
     */
    public static float calculateHeight(float radius, float heightFactor, float x, float z) {
        float val = calculateRadiusPercent(radius, x, z);
        return heightFactor * val;
    }

    /**
     * Calculate radius percent float.
     *
     * @param radius the radius
     * @param x      the x
     * @param z      the z
     * @return the float
     */
    public static float calculateRadiusPercent(float radius, float x, float z) {
        // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x,z);
        float val = Math.abs(point.length()) / radius;
        val = 1f - val;
        return val;
    }

    /**
     * Float equals boolean.
     *
     * @param a       the a
     * @param b       the b
     * @param epsilon the epsilon
     * @return the boolean
     */
    public static boolean floatEquals(float a, float b, float epsilon) {
        return a == b || Math.abs(a - b) < epsilon;
    }

    /**
     * Float less than boolean.
     *
     * @param a       the a
     * @param b       the b
     * @param epsilon the epsilon
     * @return the boolean
     */
    public static boolean floatLessThan(float a, float b, float epsilon) {
        return b - a > epsilon;
    }

    /**
     * Float greater than boolean.
     *
     * @param a       the a
     * @param b       the b
     * @param epsilon the epsilon
     * @return the boolean
     */
    public static boolean floatGreaterThan(float a, float b, float epsilon) {
        return a - b > epsilon;
    }
}
