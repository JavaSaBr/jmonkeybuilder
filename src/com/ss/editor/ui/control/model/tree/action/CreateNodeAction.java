package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;

/**
 * Действие по созданию нового узла.
 *
 * @author Ronn
 */
public class CreateNodeAction extends AbstractNodeAction {

    public CreateNodeAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_NODE;
    }

    @Override
    protected void process() {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Node node = new Node("New Node");

            final ModelNode<Node> newNode = ModelNodeFactory.createFor(node);
            final ModelNode<?> modelNode = getNode();
            modelNode.add(newNode);

            EXECUTOR_MANAGER.addFXTask(() -> {
                final ModelNodeTree nodeTree = getNodeTree();
                nodeTree.notifyAdded(modelNode, newNode);
            });
        });
    }
}
