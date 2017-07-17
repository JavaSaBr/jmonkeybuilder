package com.ss.editor.ui.control.model.tree.action.control.physics;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link RigidBodyControl}.
 *
 * @author JavaSaBr
 */
public class CreateRigidBodyControlAction extends AbstractCreateControlAction {

    /**
     * Instantiates a new Create rigid body control action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateRigidBodyControlAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.RIGID_BODY_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_RIGID_BODY;
    }

    @NotNull
    @Override
    protected RigidBodyControl createControl(@NotNull final Spatial parent) {
        final RigidBodyControl rigidBodyControl = new RigidBodyControl();
        rigidBodyControl.setEnabled(false);
        return rigidBodyControl;
    }
}
