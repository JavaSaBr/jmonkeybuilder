package com.ss.editor.ui.control.model.tree.node.light;

import com.jme3.light.Light;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.RemoveLightAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

/**
 * Базовая реализация узла со светом в дереве.
 *
 * @author Ronn
 */
public class LightModelNode<T extends Light> extends ModelNode<T> {

    public LightModelNode(final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public String getName() {
        return getElement().getClass().getSimpleName();
    }

    @Override
    public Image getIcon() {
        return Icons.LIGHT_24;
    }

    @Override
    public void fillContextMenu(final ModelNodeTree nodeTree, final ObservableList<MenuItem> items) {
        items.add(new RemoveLightAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }
}
