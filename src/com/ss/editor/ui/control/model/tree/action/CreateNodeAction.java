package com.ss.editor.ui.control.model.tree.action;

import static java.util.Objects.requireNonNull;

import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a node.
 *
 * @author JavaSaBr
 */
public class CreateNodeAction extends AbstractNodeAction {

    public CreateNodeAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.NODE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_NODE;
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer consumer = requireNonNull(nodeTree.getModelChangeConsumer());

        final Node node = new Node("New Node");

        final ModelNode<?> modelNode = getNode();
        final Node parent = (Node) modelNode.getElement();

        consumer.execute(new AddChildOperation(node, parent));
    }
}
