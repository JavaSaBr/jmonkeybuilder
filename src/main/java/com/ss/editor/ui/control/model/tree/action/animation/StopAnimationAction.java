package com.ss.editor.ui.control.model.tree.action.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.anim.AnimationTreeNode;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to stop an animation.
 *
 * @author JavaSaBr
 */
public class StopAnimationAction extends AbstractNodeAction<ModelChangeConsumer> {

    public StopAnimationAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_STOP;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.STOP_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final AnimationTreeNode modelNode = (AnimationTreeNode) getNode();
        if (modelNode.getChannel() < 0) return;

        final AnimControl control = modelNode.getControl();
        if (control == null || control.getNumChannels() <= 0) return;

        final AnimChannel channel = control.getChannel(modelNode.getChannel());
        channel.setLoopMode(LoopMode.DontLoop);

        EXECUTOR_MANAGER.addJMETask(control::clearChannels);

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        nodeTree.update(modelNode);
    }
}
