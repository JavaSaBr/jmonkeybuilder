package com.ss.editor.ui.control.tree.node.factory.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.node.impl.physics.shape.*;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of a tree node factory to make collision nodes.
 *
 * @author JavaSaBr
 */
public class CollisionTreeNodeFactory implements TreeNodeFactory {

    @Override
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof ChildCollisionShape) {
            return unsafeCast(new ChildCollisionShapeTreeNode((ChildCollisionShape) element, objectId));
        } else if (element instanceof CollisionShape) {
            if (element instanceof BoxCollisionShape) {
                return unsafeCast(new BoxCollisionShapeTreeNode((BoxCollisionShape) element, objectId));
            } else if (element instanceof CapsuleCollisionShape) {
                return unsafeCast(new CapsuleCollisionShapeTreeNode((CapsuleCollisionShape) element, objectId));
            } else if (element instanceof CompoundCollisionShape) {
                return unsafeCast(new ComputedCollisionShapeTreeNode((CompoundCollisionShape) element, objectId));
            } else if (element instanceof ConeCollisionShape) {
                return unsafeCast(new ConeCollisionShapeTreeNode((ConeCollisionShape) element, objectId));
            } else if (element instanceof CylinderCollisionShape) {
                return unsafeCast(new CylinderCollisionShapeTreeNode((CylinderCollisionShape) element, objectId));
            } else if (element instanceof GImpactCollisionShape) {
                return unsafeCast(new GImpactCollisionShapeTreeNode((GImpactCollisionShape) element, objectId));
            } else if (element instanceof HullCollisionShape) {
                return unsafeCast(new HullCollisionShapeTreeNode((HullCollisionShape) element, objectId));
            } else if (element instanceof MeshCollisionShape) {
                return unsafeCast(new MeshCollisionShapeTreeNode((MeshCollisionShape) element, objectId));
            } else if (element instanceof PlaneCollisionShape) {
                return unsafeCast(new PlaneCollisionShapeTreeNode((PlaneCollisionShape) element, objectId));
            } else if (element instanceof SphereCollisionShape) {
                return unsafeCast(new SphereCollisionShapeTreeNode((SphereCollisionShape) element, objectId));
            }
        }

        return null;
    }
}
