package com.ss.editor.ui.control.model.node.light;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.light.Light;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.RemoveLightAction;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.RenameLightOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.StringUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of {@link TreeNode} to present lights.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class LightTreeNode<T extends Light> extends TreeNode<T> {

    public LightTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final T element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? element.getClass().getSimpleName() : name;
    }

    @Override
    @FxThread
    public void changeName(@NotNull final NodeTree<?> nodeTree, @NotNull final String newName) {
        final T element = getElement();
        final ChangeConsumer consumer = notNull(nodeTree.getChangeConsumer());
        final String currentName = element.getName();
        consumer.execute(new RenameLightOperation(currentName == null ? "" : currentName, newName, element));
    }

    @Override
    @FxThread
    public boolean canEditName() {
        return true;
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.LIGHT_16;
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RemoveLightAction(nodeTree, this));
        items.add(new RenameNodeAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }
}
