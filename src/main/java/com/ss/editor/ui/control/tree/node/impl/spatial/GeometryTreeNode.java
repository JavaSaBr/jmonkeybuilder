package com.ss.editor.ui.control.tree.node.impl.spatial;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.tree.action.impl.TangentGeneratorAction;
import com.ss.editor.ui.control.tree.action.impl.geometry.GenerateLoDAction;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The implementation of the {@link SpatialTreeNode} to represent the {@link Geometry} in the editor.
 *
 * @param <T> the type of geometry.
 * @author JavaSaBr
 */
public class GeometryTreeNode<T extends Geometry> extends SpatialTreeNode<T> {

    public GeometryTreeNode(@NotNull T element, long objectId) {
        super(element, objectId);
    }

    @Override
    public @Nullable Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @Override
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull NodeTree<?> nodeTree) {

        if (!(nodeTree instanceof ModelNodeTree)) {
            return Array.empty();
        }

        var result = ArrayFactory.<TreeNode<?>>newArray(TreeNode.class);

        var geometry = getElement();
        var mesh = geometry.getMesh();
        var material = geometry.getMaterial();

        if (mesh != null) {
            result.add(FACTORY_REGISTRY.createFor(mesh));
        }

        result.add(FACTORY_REGISTRY.createFor(material));
        result.addAll(super.getChildren(nodeTree));

        return result;
    }

    @Override
    protected @NotNull Optional<Menu> createToolMenu(@NotNull NodeTree<?> nodeTree) {

        var toolActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS,
                new ImageView(Icons.INFLUENCER_16));

        toolActions.getItems()
                .addAll(new TangentGeneratorAction(nodeTree, this),
                        new GenerateLoDAction(nodeTree, this));

        return Optional.of(toolActions);
    }

    @Override
    public void add(@NotNull TreeNode<?> child) {
        super.add(child);

        if (child instanceof MeshTreeNode) {
            getElement().setMesh((Mesh) child.getElement());
        }
    }

    @Override
    public boolean canAccept(@NotNull TreeNode<?> treeNode, boolean isCopy) {
        var element = treeNode.getElement();
        return (element instanceof Material && isCopy) || super.canAccept(treeNode, isCopy);
    }

    @Override
    public void accept(@NotNull ChangeConsumer changeConsumer, @NotNull Object object, boolean isCopy) {

        var geometry = getElement();

        if (object instanceof Material) {

            var material = (Material) object;

            if (isCopy) {

                var clone = material.clone();
                var operation = new PropertyOperation<ChangeConsumer, Geometry, Material>(geometry,
                        "Material", clone, geometry.getMaterial());
                operation.setApplyHandler(Geometry::setMaterial);

                changeConsumer.execute(operation);
            }
        }

        super.accept(changeConsumer, object, isCopy);
    }
}
