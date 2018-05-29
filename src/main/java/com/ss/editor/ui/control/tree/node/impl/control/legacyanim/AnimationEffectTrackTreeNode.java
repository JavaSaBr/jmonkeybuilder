package com.ss.editor.ui.control.tree.node.impl.control.legacyanim;

import com.jme3.animation.EffectTrack;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link EffectTrack}.
 *
 * @author JavaSaBr
 */
@Deprecated
public class AnimationEffectTrackTreeNode extends AnimationTrackTreeNode<EffectTrack> {

    public AnimationEffectTrackTreeNode(@NotNull EffectTrack element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    protected @NotNull String computeName() {
        var effectTrack = getElement();
        return "Effect track : " + effectTrack.getEmitter().getName();
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.PARTICLES_16;
    }
}
