package com.ss.editor.ui.control.model.node.control.anim;

import com.jme3.animation.SpatialTrack;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of node for showing {@link SpatialTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationSpatialTrackModelNode extends AnimationTrackModelNode<SpatialTrack> {

    public AnimationSpatialTrackModelNode(final SpatialTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    protected String computeName() {
        return "SpatialTrack";
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }
}
