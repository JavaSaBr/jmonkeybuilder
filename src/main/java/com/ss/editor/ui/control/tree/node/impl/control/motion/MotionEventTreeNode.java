package com.ss.editor.ui.control.tree.node.impl.control.motion;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ControlTreeNode} to show a {@link MotionEvent} in the tree.
 *
 * @author JavaSaBr
 */
public class MotionEventTreeNode extends ControlTreeNode<MotionEvent> {

    public MotionEventTreeNode(@NotNull final MotionEvent element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MOTION_CONTROL;
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.MOTION_16;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final MotionPath path = getElement().getPath();
        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        result.add(FACTORY_REGISTRY.createFor(path));

        return result;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }
}
