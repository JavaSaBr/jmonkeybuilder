package com.ss.editor.ui.control.model.tree.node.spatial;

import com.jme3.audio.AudioNode;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link AudioNode} in the editor.
 *
 * @author JavaSaBr
 */
public class AudioModelNode extends NodeModelNode<AudioNode> {

    public AudioModelNode(@NotNull final AudioNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.AUDIO_16;
    }
}
