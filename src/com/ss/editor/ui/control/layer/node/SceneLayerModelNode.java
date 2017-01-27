package com.ss.editor.ui.control.layer.node;

import static java.util.Objects.requireNonNull;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.NodeModelNode;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.RenameNodeOperation;
import com.ss.editor.ui.control.model.tree.action.operation.scene.ChangeVisibleSceneLayerOperation;
import com.ss.editor.ui.control.model.tree.action.scene.RemoveSceneLayerAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.HideableNode;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.extension.scene.SceneLayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link SceneLayer} in the editor.
 *
 * @author JavaSaBr
 */
public class SceneLayerModelNode extends ModelNode<SceneLayer> implements HideableNode<SceneChangeConsumer> {

    public SceneLayerModelNode(@NotNull final SceneLayer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        super.fillContextMenu(nodeTree, items);

        final SceneLayer layer = getElement();

        if (!layer.isBuiltIn()) {
            items.add(new RenameNodeAction(nodeTree, this));
            items.add(new RemoveSceneLayerAction(nodeTree, this));
        }
    }

    @Override
    public void changeName(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final String newName) {

        final SceneLayer element = getElement();

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RenameNodeOperation(element.getName(), newName, element));
    }

    @NotNull
    @Override
    public String getName() {
        final String name = getElement().getName();
        return name == null ? "name is null" : name;
    }

    @Override
    public boolean canEditName() {
        return !getElement().isBuiltIn();
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.LAYERS_16;
    }

    @Override
    public boolean isHided() {
        return !getElement().isShowed();
    }

    @Override
    public void show(@NotNull final AbstractNodeTree<SceneChangeConsumer> nodeTree) {
        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeVisibleSceneLayerOperation(getElement(), true));
    }

    @Override
    public void hide(@NotNull final AbstractNodeTree<SceneChangeConsumer> nodeTree) {
        final ChangeConsumer consumer = requireNonNull(nodeTree.getChangeConsumer());
        consumer.execute(new ChangeVisibleSceneLayerOperation(getElement(), false));
    }
}
