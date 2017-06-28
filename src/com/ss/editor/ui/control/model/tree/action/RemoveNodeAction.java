package com.ss.editor.ui.control.model.tree.action;

import static java.util.Objects.requireNonNull;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to remove a node from model.
 *
 * @author JavaSaBr
 */
public class RemoveNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Remove node action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public RemoveNodeAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REMOVE_12;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final ModelNode<?> node = getNode();
        final Object element = node.getElement();
        if (!(element instanceof Spatial)) return;

        final Spatial spatial = (Spatial) element;

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveChildOperation(spatial, spatial.getParent()));
    }
}
