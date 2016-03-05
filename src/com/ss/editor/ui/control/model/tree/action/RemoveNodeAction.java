package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

/**
 * Реализация действия по удалению узла.
 *
 * @author Ronn
 */
public class RemoveNodeAction extends AbstractNodeAction {

    public RemoveNodeAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final Object element = node.getElement();

        if(!(element instanceof Spatial)) {
            return;
        }

        final Node parent = ((Spatial) element).getParent();
        parent.detachChild((Spatial) element);

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.notifyRemoved(node);
    }
}
