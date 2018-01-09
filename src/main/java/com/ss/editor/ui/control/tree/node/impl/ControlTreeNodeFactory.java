package com.ss.editor.ui.control.tree.node.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.control.model.node.control.ControlTreeNode;
import com.ss.editor.ui.control.model.node.control.LightControlTreeNode;
import com.ss.editor.ui.control.model.node.control.SkeletonControlTreeNode;
import com.ss.editor.ui.control.model.node.control.motion.MotionEventTreeNode;
import com.ss.editor.ui.control.model.node.control.physics.CharacterControlTreeNode;
import com.ss.editor.ui.control.model.node.control.physics.RagdollControlTreeNode;
import com.ss.editor.ui.control.model.node.control.physics.RigidBodyControlTreeNode;
import com.ss.editor.ui.control.model.node.control.physics.vehicle.VehicleControlTreeNode;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of a tree node factory to make control nodes.
 *
 * @author JavaSaBr
 */
public class ControlTreeNodeFactory implements TreeNodeFactory {

    @Override
    @FXThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof MotionEvent) {
            return unsafeCast(new MotionEventTreeNode((MotionEvent) element, objectId));
        } else if (element instanceof KinematicRagdollControl) {
            return unsafeCast(new RagdollControlTreeNode((KinematicRagdollControl) element, objectId));
        } else if (element instanceof VehicleControl) {
            return unsafeCast(new VehicleControlTreeNode((VehicleControl) element, objectId));
        } else if (element instanceof SkeletonControl) {
            return unsafeCast(new SkeletonControlTreeNode((SkeletonControl) element, objectId));
        } else if (element instanceof CharacterControl) {
            return unsafeCast(new CharacterControlTreeNode((CharacterControl) element, objectId));
        } else if (element instanceof RigidBodyControl) {
            return unsafeCast(new RigidBodyControlTreeNode((RigidBodyControl) element, objectId));
        }  else if (element instanceof LightControl) {
            return unsafeCast(new LightControlTreeNode((LightControl) element, objectId));
        } else if (element instanceof Control) {
            return unsafeCast(new ControlTreeNode<>((Control) element, objectId));
        }

        return null;
    }
}
