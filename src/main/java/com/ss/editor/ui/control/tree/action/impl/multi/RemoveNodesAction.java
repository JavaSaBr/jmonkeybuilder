package com.ss.editor.ui.control.tree.action.impl.multi;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.action.impl.operation.RemoveChildOperation;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.spatial.SpatialTreeNode;
import com.ss.rlib.function.TripleConsumer;
import com.ss.rlib.util.array.Array;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.ss.rlib.util.ObjectUtils.notNull;

/**
 * The action to remove nodes from model.
 *
 * @author JavaSaBr
 */
public class RemoveNodesAction extends AbstractNodeAction<ModelChangeConsumer> {

    public static final TripleConsumer<NodeTree<?>, List<MenuItem>, List<TreeNode<?>>> ACTION_FILLER = (nodeTree, menuItems, treeNodes) -> {

        if (treeNodes.stream().anyMatch(treeNode -> !(treeNode instanceof SpatialTreeNode))) {
            return;
        }

        menuItems.add(new RemoveNodesAction(nodeTree, treeNodes));
    };

    public RemoveNodesAction(@NotNull final NodeTree<?> nodeTree, @NotNull final List<TreeNode<?>> nodes) {
        super(nodeTree, nodes);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final Array<TreeNode<?>> nodes = getNodes();


        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveChildOperation(spatial, spatial.getParent()));
    }
}
