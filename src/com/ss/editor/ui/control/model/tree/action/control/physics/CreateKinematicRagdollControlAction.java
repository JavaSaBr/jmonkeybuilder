package com.ss.editor.ui.control.model.tree.action.control.physics;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link KinematicRagdollControl}.
 *
 * @author JavaSaBr
 */
public class CreateKinematicRagdollControlAction extends AbstractCreateControlAction {

    public CreateKinematicRagdollControlAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                               @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.ATOM_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_KINEMATIC_RAGDOLL;
    }

    @NotNull
    @Override
    protected Control createControl(@NotNull final Spatial parent) {

        final SkeletonControl control = parent.getControl(SkeletonControl.class);
        final Skeleton skeleton = control.getSkeleton();

        final KinematicRagdollControl ragdollControl = new KinematicRagdollControl(0.5F);

        for (int i = 0; i < skeleton.getBoneCount(); i++) {
            final Bone bone = skeleton.getBone(i);
            //FIXME how does it work? ragdollControl.addBoneName(bone.getName());
        }

        return ragdollControl;
    }
}
