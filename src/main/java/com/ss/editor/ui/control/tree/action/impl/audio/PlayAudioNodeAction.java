package com.ss.editor.ui.control.tree.action.impl.audio;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.spatial.AudioTreeNode;
import com.ss.editor.util.AudioNodeUtils;
import com.ss.editor.util.EditorUtils;
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
