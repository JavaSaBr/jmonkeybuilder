package com.ss.editor.ui.control.model.tree.action.control.physics;

import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link KinematicRagdollControl}.
 *
 * @author JavaSaBr
 */
public class CreateKinematicRagdollControlAction extends AbstractCreateControlAction {

    public CreateKinematicRagdollControlAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @FxThread
    @Override
    protected @Nullable Image getIcon() {
        return Icons.ATOM_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_KINEMATIC_RAGDOLL;
    }

    @Override
    @FxThread
    protected @NotNull Control createControl(@NotNull final Spatial parent) {
        return new KinematicRagdollControl(0.5F);
    }
}
