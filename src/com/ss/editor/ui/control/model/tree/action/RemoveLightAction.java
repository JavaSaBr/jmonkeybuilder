package com.ss.editor.ui.control.model.tree.action;

import static java.util.Objects.requireNonNull;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveLightOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to remove a light.
 *
 * @author JavaSaBr
 */
public class RemoveLightAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Remove light action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public RemoveLightAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REMOVE_12;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final ModelNode<?> node = getNode();
        final Object element = node.getElement();

        if (!(element instanceof Light)) return;

        final Light light = (Light) element;

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelNode<?> parentNode = nodeTree.findParent(node);
        if (parentNode == null) return;

        final Object parent = parentNode.getElement();
        if (!(parent instanceof Node)) return;

        final ModelChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveLightOperation(light, (Node) parent));
    }
}
