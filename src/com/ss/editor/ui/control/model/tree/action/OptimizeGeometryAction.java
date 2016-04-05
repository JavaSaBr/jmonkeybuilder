package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.OptimizeGeometryOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

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
        return Messages.MODEL_NODE_TREE_ACTION_OPTIMIZE_GEOMETRY;
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ModelNode<?> node = getNode();
        final Node oldElement = (Node) node.getElement();

        //FIXME потом надо убрать, как с клонированием разберусь
        final Node parent = oldElement.getParent();
        oldElement.removeFromParent();

        final Node newElement = (Node) oldElement.clone();

        parent.attachChild(oldElement);

        GeometryBatchFactory.optimize(newElement);

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), oldElement.getParent());

        modelChangeConsumer.execute(new OptimizeGeometryOperation(newElement, oldElement, index));
    }
}
