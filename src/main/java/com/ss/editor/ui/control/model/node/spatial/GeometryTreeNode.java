package com.ss.editor.ui.control.model.node.spatial;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.TangentGeneratorAction;
import com.ss.editor.ui.control.model.tree.action.geometry.GenerateLoDAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SpatialTreeNode} to represent the {@link Geometry} in the editor.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class GeometryTreeNode<T extends Geometry> extends SpatialTreeNode<T> {

    /**
     * Instantiates a new Geometry model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public GeometryTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @NotNull
    @Override
    public Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {
        if (!(nodeTree instanceof ModelNodeTree)) return TreeNode.EMPTY_ARRAY;

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);

        final Geometry geometry = getElement();
        final Mesh mesh = geometry.getMesh();
        final Material material = geometry.getMaterial();

        if (mesh != null) result.add(FACTORY_REGISTRY.createFor(mesh));

        result.add(FACTORY_REGISTRY.createFor(material));
        result.addAll(super.getChildren(nodeTree));

        return result;
    }

    @Nullable
    @Override
    protected Menu createToolMenu(@NotNull final NodeTree<?> nodeTree) {

        final Menu toolActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS, new ImageView(Icons.INFLUENCER_16));
        toolActions.getItems().addAll(new TangentGeneratorAction(nodeTree, this),
                        new GenerateLoDAction(nodeTree, this));

        return toolActions;
    }

    @Override
    public void add(@NotNull final TreeNode<?> child) {
        super.add(child);

        final Geometry geometry = getElement();

        if (child instanceof MeshTreeNode) {
            final Mesh element = (Mesh) child.getElement();
            geometry.setMesh(element);
        }
    }

    @Override
    public boolean canAccept(@NotNull final TreeNode<?> child, final boolean isCopy) {
        final Object element = child.getElement();
        return (element instanceof Material && isCopy) || super.canAccept(child, isCopy);
    }

    @Override
    public void accept(@NotNull final ChangeConsumer changeConsumer, @NotNull final Object object,
                       final boolean isCopy) {

        final Geometry geometry = getElement();

        if (object instanceof Material) {

            final Material material = (Material) object;

            if (isCopy) {

                final Material clone = material.clone();
                final ModelPropertyOperation<Geometry, Material> operation =
                        new ModelPropertyOperation<>(geometry, "Material", clone, geometry.getMaterial());
                operation.setApplyHandler(Geometry::setMaterial);

                changeConsumer.execute(operation);
            }
        }

        super.accept(changeConsumer, object, isCopy);
    }
}
