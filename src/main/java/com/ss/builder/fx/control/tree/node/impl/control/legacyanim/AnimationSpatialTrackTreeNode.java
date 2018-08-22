package com.ss.builder.ui.control.tree.node.impl.control.legacyanim;

import com.jme3.animation.SpatialTrack;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link SpatialTrack}.
 *
 * @author JavaSaBr
 */
@Deprecated
public class AnimationSpatialTrackTreeNode extends AnimationTrackTreeNode<SpatialTrack> {

    public AnimationSpatialTrackTreeNode(@NotNull SpatialTrack element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    protected @NotNull String computeName() {
        return "Spatial track";
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.NODE_16;
    }
}
