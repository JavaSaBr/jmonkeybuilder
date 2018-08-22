package com.ss.builder.fx.control.tree.action.impl;

import static com.ss.builder.util.EditorUtils.getDefaultLayer;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AddChildOperation;
import com.ss.builder.fx.Icons;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.model.undo.impl.AddChildOperation;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.fx.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a node.
 *
 * @author JavaSaBr
 */
public class CreateNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CreateNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.NODE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_NODE;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final NodeTree<?> nodeTree = getNodeTree();
        final Node node = new Node("New Node");

        final TreeNode<?> treeNode = getNode();
        final Node parent = (Node) treeNode.getElement();

        final ChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());
        final SceneLayer defaultLayer = EditorUtils.getDefaultLayer(consumer);

        if (defaultLayer != null) {
            SceneLayer.setLayer(defaultLayer, node);
        }

        consumer.execute(new AddChildOperation(node, parent));
    }
}
