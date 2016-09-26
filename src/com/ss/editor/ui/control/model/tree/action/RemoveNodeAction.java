package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveChildOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

/**
 * Реализация действия по удалению узла.
 *
 * @author Ronn
 */
public class RemoveNodeAction extends AbstractNodeAction {

    public RemoveNodeAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final Object element = node.getElement();
        if (!(element instanceof Spatial)) return;

        final Spatial spatial = (Spatial) element;

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), spatial.getParent());

        modelChangeConsumer.execute(new RemoveChildOperation(spatial, index));
    }
}
