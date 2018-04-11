package com.ss.editor.ui.control.tree.action.impl.animation;

import com.jme3.animation.*;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.control.anim.AnimationControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.anim.AnimationTreeNode;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.StringUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to play an animation.
 *
 * @author JavaSaBr
 */
public class PlayAnimationAction extends AbstractNodeAction<ModelChangeConsumer> implements AnimEventListener {

    public PlayAnimationAction(final NodeTree<?> nodeTree, final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_PLAY;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.PLAY_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final AnimationTreeNode modelNode = (AnimationTreeNode) getNode();
        final AnimationControlTreeNode controlModelNode = modelNode.getControlModelNode();
        if (controlModelNode == null) return;

        final Animation element = modelNode.getElement();
        final AnimControl control = modelNode.getControl();
        if (control == null) return;

        modelNode.setSpeed(controlModelNode.getSpeed());

        if (modelNode.getChannel() >= 0) {
            final AnimChannel channel = control.getChannel(modelNode.getChannel());
            channel.setSpeed(controlModelNode.getSpeed());
        } else {

            modelNode.setChannel(control.getNumChannels());

            EXECUTOR_MANAGER.addJmeTask(() -> {
                control.addListener(this);

                final AnimChannel channel = control.createChannel();
                channel.setAnim(element.getName());
                channel.setLoopMode(controlModelNode.getLoopMode());
                channel.setSpeed(controlModelNode.getSpeed());
            });
        }

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        nodeTree.update(modelNode);
    }

    @Override
    @JmeThread
    public void onAnimCycleDone(@NotNull final AnimControl control, @NotNull final AnimChannel channel,
                                @NotNull final String animName) {

        if (channel.getLoopMode() != LoopMode.DontLoop) return;

        final AnimationTreeNode modelNode = (AnimationTreeNode) getNode();
        final Animation element = modelNode.getElement();
        if (!StringUtils.equals(element.getName(), animName)) return;

        modelNode.setChannel(-1);

        EXECUTOR_MANAGER.addFxTask(() -> getNodeTree().update(modelNode));
        EXECUTOR_MANAGER.addJmeTask(control::clearChannels);
    }

    @Override
    @JmeThread
    public void onAnimChange(@NotNull final AnimControl control, @NotNull final AnimChannel channel,
                             @NotNull final String animName) {
    }
}
