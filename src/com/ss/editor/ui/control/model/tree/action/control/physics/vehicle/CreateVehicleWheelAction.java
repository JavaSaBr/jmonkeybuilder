package com.ss.editor.ui.control.model.tree.action.control.physics.vehicle;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddVehicleWheelOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import org.jetbrains.annotations.NotNull;

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
        return "Add wheel";
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final VehicleControl element = (VehicleControl) node.getElement();

        final ModelChangeConsumer changeConsumer = getNodeTree().getChangeConsumer();
        changeConsumer.execute(new AddVehicleWheelOperation(element, new Vector3f(1, 1, 1),
                new Vector3f(0, 1, 0), new Vector3f(0, 0, 1), 1, 1, false));
    }
}
