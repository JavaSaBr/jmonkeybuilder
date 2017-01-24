package com.ss.editor.ui.control.model.node.control.anim;

import com.jme3.animation.EffectTrack;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of node for showing {@link EffectTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationEffectTrackModelNode extends AnimationTrackModelNode<EffectTrack> {

    public AnimationEffectTrackModelNode(final EffectTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    protected String computeName() {
        final EffectTrack effectTrack = getElement();
        return "EffectTrack : " + effectTrack.getEmitter().getName();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }
}
