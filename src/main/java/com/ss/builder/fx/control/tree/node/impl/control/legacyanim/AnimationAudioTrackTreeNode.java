package com.ss.builder.ui.control.tree.node.impl.control.legacyanim;

import com.jme3.animation.AudioTrack;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link AudioTrack}.
 *
 * @author JavaSaBr
 */
@Deprecated
public class AnimationAudioTrackTreeNode extends AnimationTrackTreeNode<AudioTrack> {

    public AnimationAudioTrackTreeNode(@NotNull AudioTrack element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    protected @NotNull String computeName() {
        var audioTrack = getElement();
        return "Audio track : " + audioTrack.getAudio().getName();
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.AUDIO_16;
    }
}
