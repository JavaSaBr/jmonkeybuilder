package com.ss.editor.ui.control.model.tree.action.control.physics.vehicle;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link VehicleControl}.
 *
 * @author JavaSaBr
 */
public class CreateVehicleControlAction extends AbstractCreateControlAction {

    /**
     * Instantiates a new Create vehicle control action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateVehicleControlAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.VEHICLE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_VEHICLE;
    }

    @NotNull
    @Override
    protected Control createControl(@NotNull final Spatial parent) {
        return new VehicleControl(new BoxCollisionShape(new Vector3f(1F, 1F, 1F)), 1F);
    }
}
