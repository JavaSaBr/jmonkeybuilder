package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to rename a model node.
 *
 * @author JavaSaBr
 */
public class RenameNodeAction extends AbstractNodeAction {

    public RenameNodeAction(@NotNull final ModelNodeTree nodeTree, @NotNull ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.EDIT_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_RENAME;
    }

    @Override
    protected void process() {
        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.startEdit(getNode());
    }
}
