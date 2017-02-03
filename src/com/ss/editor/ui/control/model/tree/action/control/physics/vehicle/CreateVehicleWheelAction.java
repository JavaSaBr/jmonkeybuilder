package com.ss.editor.ui.control.model.tree.action.control.physics.vehicle;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.dialog.physics.vehicle.CreateVehicleWheelDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link VehicleWheel}.
 *
 * @author JavaSaBr
 */
public class CreateVehicleWheelAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CreateVehicleWheelAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_WHEEL;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.WHEEL_16;
    }

    @Override
    protected void process() {

        final AbstractNodeTree<?> nodeTree = getNodeTree();

        final ModelNode<?> node = getNode();
        final VehicleControl control = (VehicleControl) node.getElement();

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreateVehicleWheelDialog dialog = new CreateVehicleWheelDialog(nodeTree, control);
        dialog.show(scene.getWindow());
    }
}
