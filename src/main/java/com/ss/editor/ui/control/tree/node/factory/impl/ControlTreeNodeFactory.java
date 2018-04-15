package com.ss.editor.ui.control.tree.node.factory.impl;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.*;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.node.impl.control.ControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.LightControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.SkeletonControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.motion.MotionEventTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.physics.BetterCharacterControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.physics.RagdollControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.physics.RigidBodyControlTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.physics.vehicle.VehicleControlTreeNode;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of a tree node factory to make control nodes.
 *
 * @author JavaSaBr
 */
public class ControlTreeNodeFactory implements TreeNodeFactory {

    @Override
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof MotionEvent) {
            return unsafeCast(new MotionEventTreeNode((MotionEvent) element, objectId));
        } else if (element instanceof KinematicRagdollControl) {
            return unsafeCast(new RagdollControlTreeNode((KinematicRagdollControl) element, objectId));
        } else if (element instanceof VehicleControl) {
            return unsafeCast(new VehicleControlTreeNode((VehicleControl) element, objectId));
        } else if (element instanceof SkeletonControl) {
            return unsafeCast(new SkeletonControlTreeNode((SkeletonControl) element, objectId));
        } else if (element instanceof BetterCharacterControl) {
            return unsafeCast(new BetterCharacterControlTreeNode((BetterCharacterControl) element, objectId));
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
