package com.ss.builder.fx.control.tree.node.impl.light;

import com.jme3.light.LightProbe;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The tree node to present {@link LightProbe}
 *
 * @author JavaSaBr
 */
public class LightProbeTreeNode extends TreeNode<LightProbe> {

    public LightProbeTreeNode(@NotNull final LightProbe element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_LIGHT_PROBE;
    }
}
