package com.ss.editor.ui.control.model.tree;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.MoveChildOperation;
import com.ss.editor.ui.control.tree.NodeTreeCell;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

/**
 * THe implementation of {@link NodeTreeCell} to show model nodes.
 *
 * @author JavaSaBr
 */
public class ModelNodeTreeCell extends NodeTreeCell<ModelChangeConsumer, ModelNodeTree> {

    /**
     * Instantiates a new Model node tree cell.
     *
     * @param nodeTree the node tree
     */
    ModelNodeTreeCell(@NotNull final ModelNodeTree nodeTree) {
        super(nodeTree);
    }

    @Override
    protected boolean processDragDropped(@NotNull final TreeItem<TreeNode<?>> dragTreeItem, @NotNull final TreeNode<?> dragItem,
                                         @NotNull final TreeNode<?> item, final boolean isCopy,
                                         @NotNull final TreeItem<TreeNode<?>> newParentItem, @NotNull final Object element) {

        if (element instanceof Spatial) {

            final ModelNodeTree nodeTree = getNodeTree();
            final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());

            final TreeItem<TreeNode<?>> parent = dragTreeItem.getParent();

            final TreeNode<?> prevParent = parent.getValue();
            final TreeNode<?> newParent = newParentItem.getValue();
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
