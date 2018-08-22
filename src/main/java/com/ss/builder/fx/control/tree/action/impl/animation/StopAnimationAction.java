package com.ss.builder.fx.control.tree.action.impl.animation;

import com.jme3.animation.LoopMode;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.builder.fx.control.tree.node.impl.control.legacyanim.AnimationTreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to stop an animation.
 *
 * @author JavaSaBr
 */
public class StopAnimationAction extends AbstractNodeAction<ModelChangeConsumer> {

    public StopAnimationAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
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

        var modelNode = (AnimationTreeNode) getNode();
        if (modelNode.getChannel() < 0) {
            return;
        }

        var control = modelNode.getControl();
        if (control == null || control.getNumChannels() <= 0) {
            return;
        }

        var channel = control.getChannel(modelNode.getChannel());
        channel.setLoopMode(LoopMode.DontLoop);

        ExecutorManager.getInstance()
                .addJmeTask(control::clearChannels);

        getNodeTree().update(modelNode);
    }
}
