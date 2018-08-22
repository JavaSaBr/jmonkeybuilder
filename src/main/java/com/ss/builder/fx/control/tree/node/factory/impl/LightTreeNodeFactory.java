package com.ss.builder.fx.control.tree.node.factory.impl;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.jme3.light.*;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.control.tree.node.impl.light.*;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.builder.fx.control.tree.node.factory.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of a tree node factory to make light nodes.
 *
 * @author JavaSaBr
 */
public class LightTreeNodeFactory implements TreeNodeFactory {

    @Override
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof LightProbe) {
            return unsafeCast(new LightProbeTreeNode((LightProbe) element, objectId));
        } else if (element instanceof AmbientLight) {
            return unsafeCast(new AmbientLightTreeNode((AmbientLight) element, objectId));
        } else if (element instanceof DirectionalLight) {
            return unsafeCast(new DirectionalLightTreeNode((DirectionalLight) element, objectId));
        } else if (element instanceof SpotLight) {
            return unsafeCast(new SpotLightTreeNode((SpotLight) element, objectId));
        } else if (element instanceof PointLight) {
            return unsafeCast(new PointLightTreeNode((PointLight) element, objectId));
        }

        return null;
    }
}
