package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to rename a model node.
 *
 * @author JavaSaBr
 */
public class RenameNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Rename node action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public RenameNodeAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull ModelNode<?> node) {
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

    @FXThread
    @Override
    protected void process() {
        super.process();
        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        nodeTree.startEdit(getNode());
    }
}
