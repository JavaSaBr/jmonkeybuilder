package com.ss.editor.ui.control.model.node.control.physics.vehicle;

import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.physics.vehicle.RemoveVehicleWheelAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link VehicleWheel}.
 *
 * @author JavaSaBr
 */
public class VehicleWheelTreeNode extends TreeNode<VehicleWheel> {

    public VehicleWheelTreeNode(@NotNull final VehicleWheel element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public @Nullable Image getIcon() {
        return Icons.WHEEL_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final VehicleWheel element = getElement();
        final Spatial wheelSpatial = element.getWheelSpatial();
        return wheelSpatial != null ? Messages.MODEL_FILE_EDITOR_NODE_WHEEL + " [" + wheelSpatial.getName() + "]" :
                Messages.MODEL_FILE_EDITOR_NODE_WHEEL;
    }

    @Override
    @FXThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {
        super.fillContextMenu(nodeTree, items);
        items.add(new RemoveVehicleWheelAction(nodeTree, this));
    }
}
