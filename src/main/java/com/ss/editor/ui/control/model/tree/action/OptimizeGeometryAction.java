package com.ss.editor.ui.control.model.tree.action;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.OptimizeGeometryOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import jme3tools.optimize.GeometryBatchFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to optimize a geometry.
 *
 * @author JavaSaBr
 */
public class OptimizeGeometryAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Optimize geometry action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public OptimizeGeometryAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
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

    @FXThread
    @Override
    protected void process() {
        super.process();

        final NodeTree<?> nodeTree = getNodeTree();
        final TreeNode<?> node = getNode();
        final Node oldElement = (Node) node.getElement();
        final Node newElement = (Node) oldElement.clone();

        GeometryBatchFactory.optimize(newElement);

        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new OptimizeGeometryOperation(newElement, oldElement, oldElement.getParent()));
    }
}
