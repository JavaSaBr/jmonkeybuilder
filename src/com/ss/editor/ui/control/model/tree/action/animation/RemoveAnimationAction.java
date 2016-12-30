package com.ss.editor.ui.control.model.tree.action.animation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.animation.RemoveAnimationNodeOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of the {@link AbstractNodeAction} to remove an animation from the {@link AnimControl}.
 *
 * @author JavaSaBr
 */
public class RemoveAnimationAction extends AbstractNodeAction {

    public RemoveAnimationAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
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
        return Icons.REMOVE_18;
    }

    @Override
    protected void process() {

        final ModelNode<?> node = getNode();
        final Object element = node.getElement();

        if (!(element instanceof Animation)) return;
        final Animation animation = (Animation) element;

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelNode<?> parentNode = nodeTree.findParent(node);

        if (parentNode == null) {
            LOGGER.warning("not found parent node for " + node);
            return;
        }

        final AnimControl parent = (AnimControl) parentNode.getElement();

        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();
        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), parent);

        modelChangeConsumer.execute(new RemoveAnimationNodeOperation(animation, parent));
    }
}
