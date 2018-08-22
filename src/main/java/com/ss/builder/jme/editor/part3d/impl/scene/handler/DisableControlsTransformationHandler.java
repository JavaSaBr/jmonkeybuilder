package com.ss.builder.editor.part3d.impl.scene.handler;

import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.util.ControlUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The handler to disable all controls during transforming spatial.
 *
 * @author JavaSaBr
 */
public class DisableControlsTransformationHandler {

    /**
     * The saved disabled controls.
     */
    @NotNull
    private final ObjectDictionary<Spatial, Array<Control>> enabledControls;

    public DisableControlsTransformationHandler() {
        this.enabledControls = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Disable all controls before transform.
     *
     * @param spatial the spatial.
     */
    @JmeThread
    public void onPreTransform(@NotNull final Spatial spatial) {

        final Array<Control> enabled = NodeUtils.children(spatial)
                .flatMap(ControlUtils::controls)
                .filter(ControlUtils::isEnabled)
                .peek(control -> ControlUtils.setEnabled(control, false))
                .collect(toArray(Control.class));

        enabledControls.put(spatial, enabled);
    }

    /**
     * Enable disabled controls before transform.
     *
     * @param spatial the spatial.
     */
    @JmeThread
    public void onPostTransform(@NotNull final Spatial spatial) {

        final Array<Control> enabled = enabledControls.remove(spatial);
        if (enabled == null || enabled.isEmpty()) {
            return;
        }

        enabled.forEach(control -> ControlUtils.setEnabled(control, true));
        enabled.clear();
    }
}
