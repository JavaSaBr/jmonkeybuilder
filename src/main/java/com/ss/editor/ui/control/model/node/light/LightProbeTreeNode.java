package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.LightProbe;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.control.tree.node.TreeNode;
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
