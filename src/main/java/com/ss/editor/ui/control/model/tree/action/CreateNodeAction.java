package com.ss.editor.ui.control.model.tree.action;

import static com.ss.editor.util.EditorUtil.getDefaultLayer;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a node.
 *
 * @author JavaSaBr
 */
public class CreateNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Create node action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.NODE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_NODE;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final NodeTree<?> nodeTree = getNodeTree();
        final Node node = new Node("New Node");

        final TreeNode<?> treeNode = getNode();
        final Node parent = (Node) treeNode.getElement();

        final ChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());
        final SceneLayer defaultLayer = getDefaultLayer(consumer);

        if (defaultLayer != null) {
            SceneLayer.setLayer(defaultLayer, node);
        }

        consumer.execute(new AddChildOperation(node, parent));
    }
}
