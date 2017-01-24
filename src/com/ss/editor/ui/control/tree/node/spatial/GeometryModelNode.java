package com.ss.editor.ui.control.tree.node.spatial;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.TangentGeneratorAction;
import com.ss.editor.ui.control.model.tree.action.geometry.GenerateLoDAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.MeshModelNode;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.tree.node.ModelNodeFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link SpatialModelNode} for representing the {@link Geometry} in the editor.
 *
 * @author JavaSaBr
 */
public class GeometryModelNode<T extends Geometry> extends SpatialModelNode<T> {

    public GeometryModelNode(final T element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @NotNull
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
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {

        final Menu toolActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS, new ImageView(Icons.INFLUENCER_16));
        toolActions.getItems().addAll(new TangentGeneratorAction(nodeTree, this), new GenerateLoDAction(nodeTree, this));

        items.add(toolActions);

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    public boolean canAccept(@NotNull final ModelNode<?> child) {

        final Geometry geometry = getElement();
        if (geometry.getMesh() != null) return false;

        final Object element = child.getElement();
        return element instanceof Mesh;
    }

    @Override
    public void add(@NotNull final ModelNode<?> child) {
        super.add(child);

        final Geometry geometry = getElement();

        if (child instanceof MeshModelNode) {
            final Mesh element = (Mesh) child.getElement();
            geometry.setMesh(element);
        }
    }
}
