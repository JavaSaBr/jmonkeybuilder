package com.ss.editor.ui.control.tree.action.impl.animation;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.control.legacyanim.AnimationTreeNode;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.dialog.animation.ExtractSubAnimationDialog;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to manual extract a sub-animation from an animation.
 *
 * @author JavaSaBr
 */
public class ManualExtractSubAnimationAction extends AbstractNodeAction<ModelChangeConsumer> {

    public ManualExtractSubAnimationAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_MANUAL_EXTRACT_SUB_ANIMATION;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.EXTRACT_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();
        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ExtractSubAnimationDialog dialog = new ExtractSubAnimationDialog(nodeTree, (AnimationTreeNode) getNode());
        dialog.show();
    }
}
