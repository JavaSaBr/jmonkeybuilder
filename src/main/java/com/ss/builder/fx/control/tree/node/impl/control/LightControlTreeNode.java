package com.ss.builder.fx.control.tree.node.impl.control;

import com.jme3.scene.control.LightControl;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link TreeNode} to show a {@link LightControl} in the tree.
 *
 * @author JavaSaBr
 */
public class LightControlTreeNode extends ControlTreeNode<LightControl> {

    public LightControlTreeNode(@NotNull LightControl element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_LIGHT_CONTROL;
    }
}
