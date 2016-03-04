package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Node;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import jme3tools.optimize.GeometryBatchFactory;

/**
 * Реализация действия по реструктуризации геометрии.
 *
 * @author Ronn
 */
public class OptimizeGeometryAction extends AbstractNodeAction {

    public OptimizeGeometryAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected String getName() {
        return "Optimize geometry";
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final Node element = (Node) node.getElement();

        GeometryBatchFactory.optimize(element);

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.refresh(node);
        nodeTree.notifyChanged(node);
    }
}
