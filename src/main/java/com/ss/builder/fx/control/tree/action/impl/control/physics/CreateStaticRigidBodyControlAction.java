package com.ss.builder.fx.control.tree.action.impl.control.physics;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.util.ControlUtils;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.builder.util.ControlUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link RigidBodyControl}.
 *
 * @author JavaSaBr
 */
public class CreateStaticRigidBodyControlAction extends CreateRigidBodyControlAction {

    public CreateStaticRigidBodyControlAction(@NotNull final NodeTree<?> nodeTree,
                                              @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.STATIC_RIGID_BODY_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_STATIC_RIGID_BODY;
    }

    @Override
    @FxThread
    protected @NotNull RigidBodyControl createControl(@NotNull final Spatial parent) {
        final RigidBodyControl rigidBodyControl = new RigidBodyControl();
        rigidBodyControl.setEnabled(false);
        rigidBodyControl.setMass(0F);
        ControlUtils.applyScale(parent, parent.getWorldScale(), rigidBodyControl);
        return rigidBodyControl;
    }
}
