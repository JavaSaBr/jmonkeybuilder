package com.ss.editor.ui.control.model.tree.action.control;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link RigidBodyControl}.
 *
 * @author JavaSaBr
 */
public class CreateRigidBodyControlAction extends AbstractCreateControlAction {

    public CreateRigidBodyControlAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.GEAR_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_RIGID_BODY;
    }

    @NotNull
    @Override
    protected Control createControl() {
        return new RigidBodyControl();
    }
}
