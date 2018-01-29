package com.ss.editor.util;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * The utility class to work with controls.
 *
 * @author JavaSaBr
 */
public class ControlUtils {

    /**
     * Create control's stream by the spatial.
     *
     * @param spatial the spatial.
     * @return the control's stream.
     */
    @FromAnyThread
    public static @NotNull Stream<Control> controls(@NotNull final Spatial spatial) {

        final int numControls = spatial.getNumControls();
        if (numControls < 1) {
            return Stream.empty();
        }

        final Control[] controls = new Control[numControls];
        for (int i = 0; i < controls.length; i++) {
            controls[i] = spatial.getControl(i);
        }

        return Arrays.stream(controls);
    }

    /**
     * Check enabled status of the control.
     *
     * @param control the control.
     * @return true if this control is enabled.
     */
    @FromAnyThread
    public static boolean isEnabled(@NotNull final Control control) {
        if (control instanceof AbstractControl) {
            return ((AbstractControl) control).isEnabled();
        } else if (control instanceof PhysicsControl) {
            return ((PhysicsControl) control).isEnabled();
        } else {
            return true;
        }
    }

    /**
     * Change the enabled status of the control.
     *
     * @param control the control.
     * @param enabled true if the control should be enabled.
     */
    @FromAnyThread
    public static void setEnabled(@NotNull final Control control, final boolean enabled) {
        if (control instanceof AbstractControl) {
            ((AbstractControl) control).setEnabled(enabled);
        } else if (control instanceof PhysicsControl) {
            ((PhysicsControl) control).setEnabled(enabled);
        }
    }
}
