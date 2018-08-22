package com.ss.builder.ui.control.tree.node.impl.control.physics;

import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.VehicleControl;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link VehicleControl}.
 *
 * @author JavaSaBr
 */
public class RagdollControlTreeNode extends ControlTreeNode<KinematicRagdollControl> {

    public RagdollControlTreeNode(@NotNull final KinematicRagdollControl element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.DOLL_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_RAGDOLL_CONTROL;
    }
}
