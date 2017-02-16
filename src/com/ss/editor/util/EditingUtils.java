package com.ss.editor.util;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
     * Interpolate the height value based on its distance from the center (how far along
     * the radius it is).
     * The farther from the center, the less the height will be.
     * This produces a linear height falloff.
     * @param radius of the tool
     * @param heightFactor potential height value to be adjusted
     * @param x location
     * @param z location
     * @return the adjusted height value
     */
    public static float calculateHeight(float radius, float heightFactor, float x, float z) {
        float val = calculateRadiusPercent(radius, x, z);
        return heightFactor * val;
    }

    public static float calculateRadiusPercent(float radius, float x, float z) {
        // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x,z);
        float val = Math.abs(point.length()) / radius;
        val = 1f - val;
        return val;
    }
}
