package com.ss.builder.fx.control.tree.action.impl.audio;

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
import com.ss.builder.fx.control.tree.node.impl.spatial.AudioTreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to stop an audio node.
 *
 * @author JavaSaBr
 */
public class StopAudioNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public StopAudioNodeAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.STOP_16;
    }


    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_AUDIO_STOP;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        var audioModelNode = (AudioTreeNode) getNode();
        var audioNode = audioModelNode.getElement();

        ExecutorManager.getInstance()
                .addJmeTask(audioNode::stop);
    }
}
