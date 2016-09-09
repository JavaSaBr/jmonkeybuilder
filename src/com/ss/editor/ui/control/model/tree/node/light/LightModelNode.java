package com.ss.editor.ui.control.model.tree.node.light;

import com.jme3.light.Light;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.RemoveLightAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    @Override
    public String getName() {
        return getElement().getClass().getSimpleName();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.LIGHT_24;
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RemoveLightAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }
}
