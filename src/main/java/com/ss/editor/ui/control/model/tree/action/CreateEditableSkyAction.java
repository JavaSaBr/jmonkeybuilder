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

    /**
     * Instantiates a new Create sky action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateEditableSkyAction(final @NotNull NodeTree<?> nodeTree, final @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_EDITABLE_SKY;
    }

    @NotNull
    @Override
    @FXThread
    protected CreateSkyDialog createDialog() {
        return new CreateEditableSkyDialog(getNode(), getNodeTree());
    }
}
