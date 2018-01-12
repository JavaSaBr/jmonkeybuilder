package com.ss.editor.ui.control.model.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import com.ss.editor.util.NodeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SpatialElementModelPropertyControl} to edit wheel from a scene.
 *
 * @author JavaSaBr
 */
public class WheelElementModelPropertyControl extends SpatialElementModelPropertyControl<Spatial, VehicleWheel> {

    public WheelElementModelPropertyControl(@Nullable final Spatial propertyValue, @NotNull final String propertyName,
                                            @NotNull final ModelChangeConsumer changeConsumer) {
        super(Spatial.class, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull NodeSelectorDialog<Spatial> createNodeSelectorDialog() {
        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        final Spatial currentModel = changeConsumer.getCurrentModel();
        final Spatial root = NodeUtils.findSpatial(currentModel, this::checkSpatial);
        return new NodeSelectorDialog<>(notNull(root), type, this::processAdd);
    }

    /**
     * Check a spatial to have a edited wheel.
     *
     * @param spatial the spatial.
     * @return true if the spatial has this wheel.
     */
    private boolean checkSpatial(@NotNull final Spatial spatial) {

        final VehicleControl control = spatial.getControl(VehicleControl.class);
        if (control == null) return false;

        final int numWheels = control.getNumWheels();

        for (int i = 0; i < numWheels; i++) {
            final VehicleWheel wheel = control.getWheel(i);
            if (wheel == getEditObject()) return true;
        }

        return false;
    }
}
