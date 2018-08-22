package com.ss.builder.fx.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.dialog.node.selector.NodeSelectorDialog;
import com.ss.builder.util.NodeUtils;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.dialog.node.selector.NodeSelectorDialog;
import com.ss.builder.util.NodeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SpatialElementModelPropertyControl} to edit wheel from a scene.
 *
 * @author JavaSaBr
 */
public class WheelElementModelPropertyControl extends SpatialElementModelPropertyControl<Spatial, VehicleWheel> {

    public WheelElementModelPropertyControl(
            @Nullable Spatial propertyValue,
            @NotNull String propertyName,
            @NotNull ModelChangeConsumer changeConsumer
    ) {

        super(Spatial.class, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull NodeSelectorDialog<Spatial> createNodeSelectorDialog() {

        var currentModel = getChangeConsumer()
                .getCurrentModel();

        var root = NodeUtils.findSpatial(currentModel, this::checkSpatial);

        return new NodeSelectorDialog<>(notNull(root), type, this::addElement);
    }

    /**
     * Check a spatial to have a edited wheel.
     *
     * @param spatial the spatial.
     * @return true if the spatial has this wheel.
     */
    private boolean checkSpatial(@NotNull Spatial spatial) {

        var control = spatial.getControl(VehicleControl.class);

        if (control == null) {
            return false;
        }

        var numWheels = control.getNumWheels();

        for (var i = 0; i < numWheels; i++) {
            var wheel = control.getWheel(i);
            if (wheel == getEditObject()) {
                return true;
            }
        }

        return false;
    }
}
