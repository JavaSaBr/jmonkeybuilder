package com.ss.editor.ui.control.tree.node.impl.control.physics.vehicle;

import com.jme3.bullet.control.VehicleControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.control.physics.PhysicsControlTreeNode;
import com.ss.editor.ui.control.tree.action.impl.control.physics.vehicle.CreateVehicleWheelAction;
import com.ss.editor.ui.control.tree.NodeTree;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link VehicleControl}.
 *
 * @author JavaSaBr
 */
public class VehicleControlTreeNode extends PhysicsControlTreeNode<VehicleControl> {

    public VehicleControlTreeNode(@NotNull final VehicleControl element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.VEHICLE_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_VEHICLE_CONTROL;
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {

        items.add(new CreateVehicleWheelAction(nodeTree, this));

        super.fillContextMenu(nodeTree, items);
    }
}
