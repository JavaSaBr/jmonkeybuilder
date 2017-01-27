package com.ss.editor.ui.control.model.tree;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.MoveChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTreeCell;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javafx.scene.control.TreeItem;

/**
 * THe implementation of {@link AbstractNodeTreeCell} to show model nodes.
 *
 * @author JavaSaBr
 */
public class ModelNodeTreeCell extends AbstractNodeTreeCell<ModelChangeConsumer, ModelNodeTree> {

    public ModelNodeTreeCell(@NotNull final ModelNodeTree nodeTree) {
        super(nodeTree);
    }

    @Override
    protected boolean processDragDropped(@NotNull final TreeItem<ModelNode<?>> dragTreeItem, @NotNull final ModelNode<?> dragItem,
                                         @NotNull final ModelNode<?> item, final boolean isCopy,
                                         @NotNull final TreeItem<ModelNode<?>> newParentItem, @NotNull final Object element) {

        if (element instanceof Spatial) {

            final ModelNodeTree nodeTree = getNodeTree();
            final ModelChangeConsumer changeConsumer = Objects.requireNonNull(nodeTree.getChangeConsumer());

            final TreeItem<ModelNode<?>> parent = dragTreeItem.getParent();

            final ModelNode<?> prevParent = parent.getValue();
            final ModelNode<?> newParent = newParentItem.getValue();
            if (newParent == prevParent) return true;

            final Spatial spatial = (Spatial) element;
            final Node prevParentNode = (Node) prevParent.getElement();
            final Node newParentNode = (Node) newParent.getElement();
            final int childIndex = prevParentNode.getChildIndex(spatial);

            changeConsumer.execute(new MoveChildOperation(spatial, prevParentNode, newParentNode, childIndex));
            return false;
        }

        return super.processDragDropped(dragTreeItem, dragItem, item, isCopy, newParentItem, element);
    }
}
