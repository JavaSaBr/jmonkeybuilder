package com.ss.editor.ui.control.model.tree.action.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.model.node.spatial.AudioModelNode;
import com.ss.editor.util.AudioNodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to play an audio node.
 *
 * @author JavaSaBr
 */
public class PlayAudioNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Play audio node action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public PlayAudioNodeAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.PLAY_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_AUDIO_PLAY;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final AudioModelNode audioModelNode = (AudioModelNode) getNode();
        final AudioNode audioNode = audioModelNode.getElement();

        final AssetManager assetManager = EDITOR.getAssetManager();

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final AudioKey audioKey = AudioNodeUtils.getAudioKey(audioNode);
            final AudioData audioData = assetManager.loadAudio(audioKey);

            AudioNodeUtils.updateData(audioNode, audioData, audioKey);

            audioNode.play();
        });
    }
}
