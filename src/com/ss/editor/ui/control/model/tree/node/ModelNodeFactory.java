package com.ss.editor.ui.control.model.tree.node;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.ss.editor.ui.control.model.tree.node.light.AmbientLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.DirectionalLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.LightProbeModelNode;
import com.ss.editor.ui.control.model.tree.node.light.PointLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.SpotLightModelNode;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация фабрики узлов модели для дерева.
 *
 * @author Ronn
 */
public class ModelNodeFactory {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    public static <T, V extends ModelNode<T>> V createFor(T element) {

        if (element instanceof LightProbe) {
            return (V) new LightProbeModelNode((LightProbe) element, ID_GENERATOR.incrementAndGet());
        } else if (element instanceof AmbientLight) {
            return (V) new AmbientLightModelNode((AmbientLight) element, ID_GENERATOR.incrementAndGet());
        } else if (element instanceof DirectionalLight) {
            return (V) new DirectionalLightModelNode((DirectionalLight) element, ID_GENERATOR.incrementAndGet());
        } else if (element instanceof SpotLight) {
            return (V) new SpotLightModelNode((SpotLight) element, ID_GENERATOR.incrementAndGet());
        } else if (element instanceof PointLight) {
            return (V) new PointLightModelNode((PointLight) element, ID_GENERATOR.incrementAndGet());
        }

        if (element instanceof Mesh) {
            return (V) new MeshModelNode((Mesh) element, ID_GENERATOR.incrementAndGet());
        } else if (element instanceof Geometry) {
            return (V) new GeometryModelNode((Geometry) element, ID_GENERATOR.incrementAndGet());
        }

        if (element instanceof Node) {
            return (V) new NodeModelNode((Node) element, ID_GENERATOR.incrementAndGet());
        }

        return null;
    }
}
