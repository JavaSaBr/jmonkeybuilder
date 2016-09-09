package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

/**
 * Реализация действия по переименовыванию узла.
 *
 * @author Ronn
 */
public class RenameNodeAction extends AbstractNodeAction {

    public RenameNodeAction(final ModelNodeTree nodeTree, ModelNode<?> node) {
        super(nodeTree, node);
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
