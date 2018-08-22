package com.ss.builder.fx.control.tree.action.impl;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.dialog.sky.CreateEditableSkyDialog;
import com.ss.builder.fx.dialog.sky.CreateSkyDialog;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.dialog.sky.CreateEditableSkyDialog;
import com.ss.builder.fx.dialog.sky.CreateSkyDialog;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create SS sky.
 *
 * @author JavaSaBr
 */
public class CreateEditableSkyAction extends CreateSkyAction {

    public CreateEditableSkyAction(final @NotNull NodeTree<?> nodeTree, final @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_EDITABLE_SKY;
    }

    @Override
    @FxThread
    protected @NotNull CreateSkyDialog createDialog() {
        return new CreateEditableSkyDialog(getNode(), getNodeTree());
    }
}
