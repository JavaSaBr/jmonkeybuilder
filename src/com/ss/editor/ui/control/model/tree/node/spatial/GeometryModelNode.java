package com.ss.editor.ui.control.model.tree.node.spatial;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.TangentGeneratorAction;
import com.ss.editor.ui.control.model.tree.node.MeshModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация узла представляющего геометрию модели.
 *
 * @author Ronn
 */
public class GeometryModelNode extends SpatialModelNode<Geometry> {

    public GeometryModelNode(final Geometry element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @Override
    public Array<ModelNode<?>> getChildren() {

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);

        final Geometry geometry = getElement();
        final Mesh mesh = geometry.getMesh();

        if (mesh != null) result.add(ModelNodeFactory.createFor(mesh));

        result.addAll(super.getChildren());

        return result;
    }

    @Override
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {

        final Menu toolActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS);
        toolActions.getItems().addAll(new TangentGeneratorAction(nodeTree, this));

        items.add(toolActions);

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    public boolean canAccept(final ModelNode<?> node) {

        final Geometry geometry = getElement();
        if (geometry.getMesh() != null) return false;

        final Object element = node.getElement();
        return element instanceof Mesh;
    }

    @Override
    public void add(final ModelNode<?> child) {
        super.add(child);

        final Geometry geometry = getElement();

        if (child instanceof MeshModelNode) {
            final Mesh element = (Mesh) child.getElement();
            geometry.setMesh(element);
        }
    }
}
