package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.control.model.tree.dialog.sky.CreateEditableSkyDialog;
import com.ss.editor.ui.control.model.tree.dialog.sky.CreateSkyDialog;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
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
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_EDITABLE_SKY;
    }

    @Override
    @FXThread
    protected @NotNull CreateSkyDialog createDialog() {
        return new CreateEditableSkyDialog(getNode(), getNodeTree());
    }
}
