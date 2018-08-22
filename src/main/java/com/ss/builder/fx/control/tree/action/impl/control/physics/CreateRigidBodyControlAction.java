package com.ss.builder.ui.control.tree.action.impl.control.physics;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.builder.util.ControlUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.impl.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.util.ControlUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link RigidBodyControl}.
 *
 * @author JavaSaBr
 */
public class CreateRigidBodyControlAction extends AbstractCreateControlAction {

    public CreateRigidBodyControlAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.RIGID_BODY_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_RIGID_BODY;
    }

    @Override
    @FxThread
    protected @NotNull RigidBodyControl createControl(@NotNull final Spatial parent) {
        final RigidBodyControl rigidBodyControl = new RigidBodyControl();
        rigidBodyControl.setEnabled(false);
        ControlUtils.applyScale(parent, parent.getWorldScale(), rigidBodyControl);
        return rigidBodyControl;
    }
}
