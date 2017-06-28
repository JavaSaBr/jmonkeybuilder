package com.ss.editor.ui.control.model.tree.action.animation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.animation.RemoveAnimationNodeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The implementation of the {@link AbstractNodeAction} to remove an animation from the {@link AnimControl}.
 *
 * @author JavaSaBr
 */
public class RemoveAnimationAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Remove animation action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public RemoveAnimationAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
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
        return Icons.REMOVE_12;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final ModelNode<?> node = getNode();
        final Object element = node.getElement();

        if (!(element instanceof Animation)) return;
        final Animation animation = (Animation) element;

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelNode<?> parentNode = nodeTree.findParent(node);

        if (parentNode == null) {
            LOGGER.warning("not found parent node for " + node);
            return;
        }

        final AnimControl parent = (AnimControl) parentNode.getElement();

        final ModelChangeConsumer changeConsumer = Objects.requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveAnimationNodeOperation(animation, parent));
    }
}
