package com.ss.editor.ui.control.model.tree.node;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.CreateBoxAction;
import com.ss.editor.ui.control.model.tree.action.CreateNodeAction;
import com.ss.editor.ui.control.model.tree.action.CreateQuadAction;
import com.ss.editor.ui.control.model.tree.action.CreateSkyAction;
import com.ss.editor.ui.control.model.tree.action.CreateSphereAction;
import com.ss.editor.ui.control.model.tree.action.LoadModelAction;
import com.ss.editor.ui.control.model.tree.action.OptimizeGeometryAction;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.util.GeomUtils;

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
public class NodeModelNode extends ModelNode<Node> {

    public NodeModelNode(final Node element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {

        final Menu toolActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_TOOLS);
        toolActions.getItems().addAll(new OptimizeGeometryAction(nodeTree, this));

        final Menu createPrimitiveActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE);
        createPrimitiveActions.getItems().addAll(new CreateBoxAction(nodeTree, this), new CreateSphereAction(nodeTree, this), new CreateQuadAction(nodeTree, this));

        final Menu createActions = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE);
        createActions.getItems().addAll(new CreateNodeAction(nodeTree, this), new CreateSkyAction(nodeTree, this), createPrimitiveActions);

        items.add(toolActions);
        items.add(createActions);
        items.add(new LoadModelAction(nodeTree, this));
        items.add(new RenameNodeAction(nodeTree, this));

        super.fillContextMenu(nodeTree, items);
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

        return result;
    }

    @Override
    public boolean canAccept(final ModelNode<?> node) {

        if (node == this) {
            return false;
        }

        final Object element = node.getElement();
        return element instanceof Spatial && GeomUtils.canAttach(getElement(), (Spatial) element);
    }

    @Override
    public void add(final ModelNode<?> child) {
        final Spatial element = (Spatial) child.getElement();
        final Node node = getElement();
        node.attachChildAt(element, 0);
    }

    @Override
    public void remove(final ModelNode<?> child) {
        final Spatial element = (Spatial) child.getElement();
        final Node node = getElement();
        node.detachChild(element);
    }

    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }
}
