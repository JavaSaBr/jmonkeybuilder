package com.ss.editor.ui.control.model.node.control.anim;

import com.jme3.animation.AudioTrack;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link AudioTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationAudioTrackModelNode extends AnimationTrackModelNode<AudioTrack> {

    /**
     * Instantiates a new Animation audio track model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AnimationAudioTrackModelNode(@NotNull final AudioTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    protected String computeName() {
        final AudioTrack audioTrack = getElement();
        return "Audio track : " + audioTrack.getAudio().getName();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.AUDIO_16;
    }
}
