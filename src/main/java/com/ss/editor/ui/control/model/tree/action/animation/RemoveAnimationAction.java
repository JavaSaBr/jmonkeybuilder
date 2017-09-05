package com.ss.editor.ui.control.model.tree.action.animation;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.animation.RemoveAnimationNodeOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public RemoveAnimationAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
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

        final TreeNode<?> node = getNode();
        final Object element = node.getElement();

        if (!(element instanceof Animation)) return;
        final Animation animation = (Animation) element;

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final TreeNode<?> parentNode = nodeTree.findParent(node);

        if (parentNode == null) {
            LOGGER.warning("not found parent node for " + node);
            return;
        }

        final AnimControl parent = (AnimControl) parentNode.getElement();

        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveAnimationNodeOperation(animation, parent));
    }
}
