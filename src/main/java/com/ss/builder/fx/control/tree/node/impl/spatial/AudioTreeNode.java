package com.ss.builder.fx.control.tree.node.impl.spatial;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.audio.AudioSource.Status;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.model.ModelNodeTree;
import com.ss.builder.fx.control.tree.action.impl.audio.PlayAudioNodeAction;
import com.ss.builder.fx.control.tree.action.impl.audio.StopAudioNodeAction;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.model.ModelNodeTree;
import com.ss.builder.fx.control.tree.action.impl.audio.PlayAudioNodeAction;
import com.ss.builder.fx.control.tree.action.impl.audio.StopAudioNodeAction;
import com.ss.builder.fx.control.tree.NodeTree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

/**
 * The implementation of the {@link NodeTreeNode} for representing the {@link AudioNode} in the editor.
 *
 * @author JavaSaBr
 */
public class AudioTreeNode extends NodeTreeNode<AudioNode> {

    public AudioTreeNode(@NotNull AudioNode element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {

        if (!(nodeTree instanceof ModelNodeTree)) {
            return;
        }

        var element = getElement();
        var audioData = element.getAudioData();
        var status = element.getStatus();

        if (audioData != null && status != Status.Playing) {
            items.add(new PlayAudioNodeAction(nodeTree, this));
        } else if (audioData != null) {
            items.add(new StopAudioNodeAction(nodeTree, this));
        }

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.AUDIO_16;
    }
}
