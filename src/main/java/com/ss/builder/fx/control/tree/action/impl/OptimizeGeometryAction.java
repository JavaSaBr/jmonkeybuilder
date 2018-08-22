package com.ss.builder.fx.control.tree.action.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.OptimizeGeometryOperation;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.model.undo.impl.OptimizeGeometryOperation;
import com.ss.builder.fx.control.tree.node.TreeNode;
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

    public OptimizeGeometryAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_OPTIMIZE_GEOMETRY;
    }

    @Override
    @FxThread
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
