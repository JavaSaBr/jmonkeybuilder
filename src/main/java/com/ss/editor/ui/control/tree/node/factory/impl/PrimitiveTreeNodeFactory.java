package com.ss.editor.ui.control.tree.node.factory.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.node.impl.BufferTreeNode;
import com.ss.editor.ui.control.tree.node.impl.PositionTreeNode;
import com.ss.editor.ui.control.tree.node.impl.VertexBufferTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.motion.MotionPathTreeNode;
import com.ss.editor.ui.control.tree.node.impl.control.physics.vehicle.VehicleWheelTreeNode;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

/**
 * The implementation of a tree node factory to make primitive nodes.
 *
 * @author JavaSaBr
 */
public class PrimitiveTreeNodeFactory implements TreeNodeFactory {

    public static final int PRIORITY = 1;

    @Override
    @FxThread
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
    @FxThread
    public int getPriority() {
        return PRIORITY;
    }
}
