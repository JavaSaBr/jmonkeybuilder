package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveControlOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of the {@link AbstractNodeAction} for removing control from the {@link Spatial}.
 *
 * @author JavaSaBr
 */
public class RemoveControlAction extends AbstractNodeAction {

    public RemoveControlAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
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
        return Icons.REMOVE_18;
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final Object element = node.getElement();

        if (!(element instanceof Control)) return;
        final Control control = (Control) element;

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelNode<?> parentNode = nodeTree.findParent(node);

        if (parentNode == null) {
            LOGGER.warning("not found parent node for " + node);
            return;
        }

        final Object parent = parentNode.getElement();

        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();
        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), parent);

        modelChangeConsumer.execute(new RemoveControlOperation(control, index));
    }
}
