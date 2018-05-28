package com.ss.editor.ui.control.tree.node.impl.control;

import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.Named;
import com.ss.editor.extension.scene.control.EditableControl;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.impl.RemoveControlAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} to show a {@link Control} in the tree.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class ControlTreeNode<T extends Control> extends TreeNode<T> {

    public ControlTreeNode(@NotNull T element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {
        items.add(new RemoveControlAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.GEAR_16;
    }

    @Override
    @FxThread
    public boolean canCopy() {
        return true;
    }

    @Override
    @FxThread
    public boolean canMove() {
        return true;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {

        var element = getElement();

        if (element instanceof EditableControl) {
            return ((EditableControl) element).getName();
        } else if (element instanceof Named) {
            return ((Named) element).getName();
        }

        return element.getClass().getSimpleName();
    }
}
