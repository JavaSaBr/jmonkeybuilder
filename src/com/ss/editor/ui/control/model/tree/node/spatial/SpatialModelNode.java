package com.ss.editor.ui.control.model.tree.node.spatial;

import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.state.editor.impl.model.ModelEditorState;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.RemoveNodeAction;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.RenameNodeOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;
import com.ss.editor.ui.control.model.tree.node.control.ControlModelNode;
import com.ss.editor.ui.control.model.tree.node.light.LightModelNode;
import com.ss.editor.util.GeomUtils;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Базовая реализация структурного элемента дерева.
 *
 * @author Ronn
 */
public class SpatialModelNode<T extends Spatial> extends ModelNode<T> {

    public SpatialModelNode(final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {
        items.add(new RenameNodeAction(nodeTree, this));

        Node parent = getElement().getParent();

        if (parent != null && parent.getUserData(ModelEditorState.class.getName()) != Boolean.TRUE) {
            items.add(new RemoveNodeAction(nodeTree, this));
        }

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    public String getName() {
        return getElement().getName();
    }

    @Override
    public boolean canEditName() {
        return true;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public void changeName(final ModelNodeTree nodeTree, final String newName) {
        super.changeName(nodeTree, newName);

        final Spatial spatial = getElement();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), spatial);

        modelChangeConsumer.execute(new RenameNodeOperation(spatial.getName(), newName, index));
    }

    @Override
    public Array<ModelNode<?>> getChildren() {

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);

        final Spatial element = getElement();

        final LightList lightList = element.getLocalLightList();
        lightList.forEach(light -> result.add(ModelNodeFactory.createFor(light)));

        final int numControls = element.getNumControls();

        for (int i = 0; i < numControls; i++) {
            final Control control = element.getControl(i);
            result.add(ModelNodeFactory.createFor(control));
        }

        return result;
    }

    @Override
    public void add(final ModelNode<?> child) {
        super.add(child);

        final T element = getElement();

        if (child instanceof LightModelNode) {
            final Light light = (Light) child.getElement();
            element.addLight(light);
        } else if (child instanceof ControlModelNode) {
            final Control control = (Control) child.getElement();
            element.addControl(control);
        }
    }

    @Override
    public void remove(final ModelNode<?> child) {
        super.remove(child);

        final T element = getElement();

        if (child instanceof LightModelNode) {
            final Light light = (Light) child.getElement();
            element.removeLight(light);
        } else if (child instanceof ControlModelNode) {
            final Control control = (Control) child.getElement();
            element.removeControl(control);
        }
    }
}
