package com.ss.editor.ui.control.model.tree.action;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveLightOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to remove a light.
 *
 * @author JavaSaBr
 */
public class RemoveLightAction extends AbstractNodeAction<ModelChangeConsumer> {

    public RemoveLightAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    @FXThread
    protected void process() {
        super.process();

        final TreeNode<?> node = getNode();
        final Object element = node.getElement();

        if (!(element instanceof Light)) return;

        final Light light = (Light) element;

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final TreeNode<?> parentNode = nodeTree.findParent(node);
        if (parentNode == null) return;

        final Object parent = parentNode.getElement();
        if (!(parent instanceof Node)) return;

        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveLightOperation(light, (Node) parent));
    }
}
