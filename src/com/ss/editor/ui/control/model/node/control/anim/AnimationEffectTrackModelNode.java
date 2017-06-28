package com.ss.editor.ui.control.model.node.control.anim;

import com.jme3.animation.EffectTrack;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link EffectTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationEffectTrackModelNode extends AnimationTrackModelNode<EffectTrack> {

    /**
     * Instantiates a new Animation effect track model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AnimationEffectTrackModelNode(@NotNull final EffectTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    protected String computeName() {
        final EffectTrack effectTrack = getElement();
        return "Effect track : " + effectTrack.getEmitter().getName();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }
}
