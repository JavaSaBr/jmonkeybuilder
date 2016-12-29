package com.ss.editor.ui.control.model.tree.node.control.anim;

import com.jme3.animation.AudioTrack;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of node for showing {@link AudioTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationAudioTrackModelNode extends AnimationTrackModelNode<AudioTrack> {

    public AnimationAudioTrackModelNode(final AudioTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        final AudioTrack audioTrack = getElement();
        return "AudioTrack : " + audioTrack.getAudio().getName();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.AUDIO_16;
    }
}
