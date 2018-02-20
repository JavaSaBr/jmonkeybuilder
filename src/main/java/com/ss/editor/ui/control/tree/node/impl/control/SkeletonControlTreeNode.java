package com.ss.editor.ui.control.tree.node.impl.control;

import com.jme3.animation.SkeletonControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link SkeletonControl}.
 *
 * @author JavaSaBr
 */
public class SkeletonControlTreeNode extends ControlTreeNode<SkeletonControl> {

    public SkeletonControlTreeNode(@NotNull final SkeletonControl element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.SKELETON_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_SKELETON_CONTROL;
    }
}
