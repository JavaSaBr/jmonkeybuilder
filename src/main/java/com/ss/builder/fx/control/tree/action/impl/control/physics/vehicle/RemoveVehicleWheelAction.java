package com.ss.builder.fx.control.tree.action.impl.control.physics.vehicle;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.RemoveVehicleWheelOperation;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.model.undo.impl.RemoveVehicleWheelOperation;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to remove a vehicle wheel from a control.
 *
 * @author JavaSaBr
 */
public class RemoveVehicleWheelAction extends AbstractNodeAction<ModelChangeConsumer> {

    public RemoveVehicleWheelAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @Override
    @FxThread
    protected void process() {

        final TreeNode<?> node = getNode();
        final Object element = node.getElement();
        final TreeNode<?> nodeParent = notNull(node.getParent());
        final VehicleControl vehicleControl = (VehicleControl) nodeParent.getElement();
        final VehicleWheel vehicleWheel = (VehicleWheel) element;

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveVehicleWheelOperation(vehicleControl, vehicleWheel));
    }
}
