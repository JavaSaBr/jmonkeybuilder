package com.ss.editor.ui.control.tree.action.impl.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.spatial.AudioTreeNode;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.util.AudioNodeUtils;
import com.ss.editor.util.EditorUtil;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to play an audio node.
 *
 * @author JavaSaBr
 */
public class PlayAudioNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public PlayAudioNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
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

        final AudioTreeNode audioModelNode = (AudioTreeNode) getNode();
        final AudioNode audioNode = audioModelNode.getElement();

        final AssetManager assetManager = EditorUtil.getAssetManager();

        EXECUTOR_MANAGER.addJmeTask(() -> {

            final AudioKey audioKey = AudioNodeUtils.getAudioKey(audioNode);
            final AudioData audioData = assetManager.loadAudio(audioKey);

            AudioNodeUtils.updateData(audioNode, audioData, audioKey);

            audioNode.play();
        });
    }
}
