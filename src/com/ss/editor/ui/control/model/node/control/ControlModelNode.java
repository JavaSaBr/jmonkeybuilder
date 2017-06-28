package com.ss.editor.ui.control.model.node.control;

import com.jme3.scene.control.Control;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.RemoveControlAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.extension.scene.control.EditableControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

/**
 * The implementation of the {@link ModelNode} to show a {@link Control} in the tree.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class ControlModelNode<T extends Control> extends ModelNode<T> {

    /**
     * Instantiates a new Control model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ControlModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RemoveControlAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.GEAR_16;
    }

    @NotNull
    @Override
    public String getName() {

        final T element = getElement();

        if (element instanceof EditableControl) {
            return ((EditableControl) element).getName();
        }

        return element.getClass().getSimpleName();
    }
}
