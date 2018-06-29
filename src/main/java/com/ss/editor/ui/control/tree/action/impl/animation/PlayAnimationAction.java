package com.ss.editor.ui.control.tree.action.impl.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.legacyanim.AnimationTreeNode;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to play an animation.
 *
 * @author JavaSaBr
 */
public class PlayAnimationAction extends AbstractNodeAction<ModelChangeConsumer> implements AnimEventListener {

    public PlayAnimationAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
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

        var modelNode = (AnimationTreeNode) getNode();
        var controlModelNode = modelNode.getControlModelNode();

        if (controlModelNode == null) {
            return;
        }

        var element = modelNode.getElement();
        var control = modelNode.getControl();

        if (control == null) {
            return;
        }

        modelNode.setSpeed(controlModelNode.getSpeed());

        if (modelNode.getChannel() >= 0) {
            var channel = control.getChannel(modelNode.getChannel());
            channel.setSpeed(controlModelNode.getSpeed());
        } else {

            modelNode.setChannel(control.getNumChannels());

            ExecutorManager.getInstance().addJmeTask(() -> {
                control.addListener(this);

                var channel = control.createChannel();
                channel.setAnim(element.getName());
                channel.setLoopMode(controlModelNode.getLoopMode());
                channel.setSpeed(controlModelNode.getSpeed());
            });
        }

        getNodeTree().update(modelNode);
    }

    @Override
    @JmeThread
    public void onAnimCycleDone(@NotNull AnimControl control, @NotNull AnimChannel channel, @NotNull String animName) {

        if (channel.getLoopMode() != LoopMode.DontLoop) {
            return;
        }

        var modelNode = (AnimationTreeNode) getNode();
        var element = modelNode.getElement();

        if (!StringUtils.equals(element.getName(), animName)) {
            return;
        }

        modelNode.setChannel(-1);

        ExecutorManager.getInstance()
                .addFxTask(() -> getNodeTree().update(modelNode));

        ExecutorManager.getInstance()
                .addJmeTask(control::clearChannels);
    }

    @Override
    @JmeThread
    public void onAnimChange(@NotNull AnimControl control, @NotNull AnimChannel channel, @NotNull String animName) {
    }
}
