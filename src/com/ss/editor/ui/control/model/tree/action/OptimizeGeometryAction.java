package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.OptimizeGeometryOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import jme3tools.optimize.GeometryBatchFactory;

/**
 * The action to optimize a geometry.
 *
 * @author JavaSaBr
 */
public class OptimizeGeometryAction extends AbstractNodeAction {

    public OptimizeGeometryAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @NotNull
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
        final Node newElement = (Node) oldElement.clone();

        GeometryBatchFactory.optimize(newElement);

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), oldElement.getParent());

        modelChangeConsumer.execute(new OptimizeGeometryOperation(newElement, oldElement, index));
    }
}
