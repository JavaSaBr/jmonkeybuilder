package com.ss.editor.ui.control.model.node.control;

import com.jme3.animation.SkeletonControl;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link SkeletonControl}.
 *
 * @author JavaSaBr
 */
public class SkeletonControlModelNode extends ControlModelNode<SkeletonControl> {

    /**
     * Instantiates a new Skeleton control model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public SkeletonControlModelNode(@NotNull final SkeletonControl element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.SKELETON_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_SKELETON_CONTROL;
    }
}
