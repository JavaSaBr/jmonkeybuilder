package com.ss.editor.ui.control.model.node.control.anim;

import com.jme3.animation.SpatialTrack;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link SpatialTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationSpatialTrackModelNode extends AnimationTrackModelNode<SpatialTrack> {

    /**
     * Instantiates a new Animation spatial track model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AnimationSpatialTrackModelNode(@NotNull final SpatialTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    protected String computeName() {
        return "Spatial track";
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }
}
