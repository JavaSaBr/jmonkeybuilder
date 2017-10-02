package com.ss.editor.ui.control.tree.node.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.control.model.node.BufferTreeNode;
import com.ss.editor.ui.control.model.node.PositionTreeNode;
import com.ss.editor.ui.control.model.node.VertexBufferTreeNode;
import com.ss.editor.ui.control.model.node.control.motion.MotionPathTreeNode;
import com.ss.editor.ui.control.model.node.control.physics.vehicle.VehicleWheelTreeNode;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

/**
 * The implementation of a tree node factory to make primitive nodes.
 *
 * @author JavaSaBr
 */
public class PrimitiveTreeNodeFactory implements TreeNodeFactory {

    @Override
    @FXThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof Vector3f) {
            return unsafeCast(new PositionTreeNode((Vector3f) element, objectId));
        } else if (element instanceof VertexBuffer) {
            return unsafeCast(new VertexBufferTreeNode((VertexBuffer) element, objectId));
        } else if (element instanceof Buffer) {
            return unsafeCast(new BufferTreeNode((Buffer) element, objectId));
        } else if (element instanceof VehicleWheel) {
            return unsafeCast(new VehicleWheelTreeNode((VehicleWheel) element, objectId));
        } else if (element instanceof MotionPath) {
            return unsafeCast(new MotionPathTreeNode((MotionPath) element, objectId));
        }

        return null;
    }

    @Override
    @FXThread
    public int getOrder() {
        return 1;
    }
}
