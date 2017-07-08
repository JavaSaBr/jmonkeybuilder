package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.Light;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.RemoveLightAction;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import com.ss.rlib.util.StringUtils;

/**
 * The base implementation of {@link ModelNode} to present lights.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class LightModelNode<T extends Light> extends ModelNode<T> {

    /**
     * Instantiates a new Light model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public LightModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        final T element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? element.getClass().getSimpleName() : name;
    }

    @Override
    public void changeName(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final String newName) {
        final T element = getElement();
        element.setName(newName);
    }

    @Override
    public boolean canEditName() {
        return true;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.LIGHT_16;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RemoveLightAction(nodeTree, this));
        items.add(new RenameNodeAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }
}
