package com.ss.editor.ui.control.model.tree.node;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация фабрики узлов модели для дерева.
 *
 * @author Ronn
 */
public class ModelNodeFactory {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    public static <T, V extends ModelNode<T>> V createFor(T element) {

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
