package com.ss.editor.ui.control.model.tree.node.spatial;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AddAmbientLightAction;
import com.ss.editor.ui.control.model.tree.action.AddDirectionLightAction;
import com.ss.editor.ui.control.model.tree.action.AddPointLightAction;
import com.ss.editor.ui.control.model.tree.action.AddSpotLightAction;
import com.ss.editor.ui.control.model.tree.action.CreateBoxAction;
import com.ss.editor.ui.control.model.tree.action.CreateNodeAction;
import com.ss.editor.ui.control.model.tree.action.CreateQuadAction;
import com.ss.editor.ui.control.model.tree.action.CreateSkyAction;
import com.ss.editor.ui.control.model.tree.action.CreateSphereAction;
import com.ss.editor.ui.control.model.tree.action.LoadModelAction;
import com.ss.editor.ui.control.model.tree.action.OptimizeGeometryAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация узла представляющего com.jme3.scene.Node.
 *
 * @author Ronn
 */
public class NodeModelNode extends SpatialModelNode<Node> {

    public NodeModelNode(final Node element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {

        final Menu toolMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS);
        toolMenu.getItems().addAll(new OptimizeGeometryAction(nodeTree, this));

        final Menu createMenu = createCreationMenu(nodeTree);

        items.add(toolMenu);
        items.add(createMenu);
        items.add(new LoadModelAction(nodeTree, this));

        super.fillContextMenu(nodeTree, items);
    }

    @NotNull
    @Override
    protected Menu createCreationMenu(final ModelNodeTree nodeTree) {

        final Menu createPrimitiveMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE);
        createPrimitiveMenu.getItems().addAll(new CreateBoxAction(nodeTree, this), new CreateSphereAction(nodeTree, this), new CreateQuadAction(nodeTree, this));

        final Menu addLightMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_LIGHT);
        addLightMenu.getItems().addAll(new AddSpotLightAction(nodeTree, this), new AddPointLightAction(nodeTree, this), new AddAmbientLightAction(nodeTree, this), new AddDirectionLightAction(nodeTree, this));

        final Menu menu = super.createCreationMenu(nodeTree);
        menu.getItems().addAll(new CreateNodeAction(nodeTree, this), new CreateSkyAction(nodeTree, this), createPrimitiveMenu, addLightMenu);

        return menu;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public Array<ModelNode<?>> getChildren() {

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);

        final Node element = getElement();
        final List<Spatial> children = element.getChildren();
        children.forEach(spatial -> result.add(ModelNodeFactory.createFor(spatial)));

        result.addAll(super.getChildren());

        return result;
    }

    @Override
    public boolean canAccept(final ModelNode<?> child) {
        if (child == this) return false;

        final Object element = child.getElement();
        return element instanceof Spatial && GeomUtils.canAttach(getElement(), (Spatial) element);
    }

    @Override
    public void add(final ModelNode<?> child) {
        super.add(child);

        final Node node = getElement();

        if (child instanceof SpatialModelNode) {
            final Spatial element = (Spatial) child.getElement();
            node.attachChildAt(element, 0);
        }
    }

    @Override
    public void remove(final ModelNode<?> child) {
        super.remove(child);

        final Node node = getElement();

        if (child instanceof SpatialModelNode) {
            final Spatial element = (Spatial) child.getElement();
            node.detachChild(element);
        }
    }

    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }
}
