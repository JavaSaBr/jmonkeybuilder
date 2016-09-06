package com.ss.editor.ui.control.model.tree.node;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.ui.control.model.tree.node.control.ControlModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationModelNode;
import com.ss.editor.ui.control.model.tree.node.light.AmbientLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.DirectionalLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.LightProbeModelNode;
import com.ss.editor.ui.control.model.tree.node.light.PointLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.SpotLightModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.GeometryModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.NodeModelNode;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

import static rlib.util.ClassUtils.unsafeCast;

/**
 * Реализация фабрики узлов модели для дерева.
 *
 * @author Ronn
 */
public class ModelNodeFactory {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @NotNull
    public static <T, V extends ModelNode<T>> V createFor(@NotNull final T element) {

        if (element instanceof Animation) {
            return unsafeCast(new AnimationModelNode((Animation) element, ID_GENERATOR.incrementAndGet()));
        }

        if (element instanceof AnimControl) {
            return unsafeCast(new AnimationControlModelNode((AnimControl) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof Control) {
            return unsafeCast(new ControlModelNode<>((Control) element, ID_GENERATOR.incrementAndGet()));
        }

        if (element instanceof LightProbe) {
            return unsafeCast(new LightProbeModelNode((LightProbe) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof AmbientLight) {
            return unsafeCast(new AmbientLightModelNode((AmbientLight) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof DirectionalLight) {
            return unsafeCast(new DirectionalLightModelNode((DirectionalLight) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof SpotLight) {
            return unsafeCast(new SpotLightModelNode((SpotLight) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof PointLight) {
            return unsafeCast(new PointLightModelNode((PointLight) element, ID_GENERATOR.incrementAndGet()));
        }

        if (element instanceof Mesh) {
            return unsafeCast(new MeshModelNode((Mesh) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof Geometry) {
            return unsafeCast(new GeometryModelNode((Geometry) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof Node) {
            return unsafeCast(new NodeModelNode((Node) element, ID_GENERATOR.incrementAndGet()));
        }

        throw new IllegalArgumentException("unknown " + element);
    }
}
