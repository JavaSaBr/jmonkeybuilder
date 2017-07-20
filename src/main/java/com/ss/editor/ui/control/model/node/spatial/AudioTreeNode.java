package com.ss.editor.ui.control.model.node.spatial;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.audio.PlayAudioNodeAction;
import com.ss.editor.ui.control.model.tree.action.audio.StopAudioNodeAction;
import com.ss.editor.ui.control.tree.NodeTree;

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

    /**
     * Instantiates a new Audio model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AudioTreeNode(@NotNull final AudioNode element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        if (!(nodeTree instanceof ModelNodeTree)) return;

        final AudioNode element = getElement();
        final AudioData audioData = element.getAudioData();
        final AudioSource.Status status = element.getStatus();

        if (audioData != null && status != AudioSource.Status.Playing) {
            items.add(new PlayAudioNodeAction(nodeTree, this));
        } else if (audioData != null) {
            items.add(new StopAudioNodeAction(nodeTree, this));
        }

        super.fillContextMenu(nodeTree, items);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.AUDIO_16;
    }
}
