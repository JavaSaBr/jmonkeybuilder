package com.ss.editor.ui.control.model.node.control.physics.vehicle;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link VehicleWheel}.
 *
 * @author JavaSaBr
 */
public class VehicleWheelModelNode extends ModelNode<VehicleWheel> {

    public VehicleWheelModelNode(@NotNull final VehicleWheel element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.WHEEL_16;
    }

    @NotNull
    @Override
    public String getName() {
        return "Wheel";
    }
}
