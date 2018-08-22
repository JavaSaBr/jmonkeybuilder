package com.ss.builder.fx.control.tree.action.impl.audio;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.util.AudioNodeUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.builder.fx.control.tree.node.impl.spatial.AudioTreeNode;
import com.ss.builder.util.AudioNodeUtils;
import com.ss.builder.util.EditorUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to play an audio node.
 *
 * @author JavaSaBr
 */
public class PlayAudioNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public PlayAudioNodeAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.PLAY_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_AUDIO_PLAY;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        ExecutorManager.getInstance()
                .addJmeTask(this::play);
    }

    @JmeThread
    private void play() {

        var audioModelNode = (AudioTreeNode) getNode();
        var audioNode = audioModelNode.getElement();
        var audioKey = AudioNodeUtils.getAudioKey(audioNode);
        var audioData = EditorUtils.getAssetManager()
                .loadAudio(audioKey);

        AudioNodeUtils.updateData(audioNode, audioData, audioKey);

        audioNode.play();
    }
}
