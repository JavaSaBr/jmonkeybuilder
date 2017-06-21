package com.ss.editor.ui.control.model.node.spatial;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.TangentGeneratorAction;
import com.ss.editor.ui.control.model.tree.action.geometry.GenerateLoDAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.tree.node.ModelNodeFactory;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link SpatialModelNode} to represent the {@link Geometry} in the editor.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class GeometryModelNode<T extends Geometry> extends SpatialModelNode<T> {

    /**
     * Instantiates a new Geometry model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public GeometryModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        if (!(nodeTree instanceof ModelNodeTree)) return ModelNode.EMPTY_ARRAY;

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);

        final Geometry geometry = getElement();
        final Mesh mesh = geometry.getMesh();
        if (mesh != null) result.add(ModelNodeFactory.createFor(mesh));

        result.addAll(super.getChildren(nodeTree));

        return result;
    }

    @Nullable
    @Override
    protected Menu createToolMenu(@NotNull final AbstractNodeTree<?> nodeTree) {

        final Menu toolActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS, new ImageView(Icons.INFLUENCER_16));
        toolActions.getItems().addAll(new TangentGeneratorAction(nodeTree, this),
                        new GenerateLoDAction(nodeTree, this));

        return toolActions;
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
