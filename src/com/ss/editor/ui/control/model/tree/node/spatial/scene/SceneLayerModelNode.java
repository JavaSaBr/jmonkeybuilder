package com.ss.editor.ui.control.model.tree.node.spatial.scene;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.scene.RemoveSceneLayerAction;
import com.ss.editor.ui.control.model.tree.node.Hideable;
import com.ss.editor.ui.control.model.tree.node.spatial.NodeModelNode;
import com.ss.extension.scene.SceneLayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link SceneLayer} in the editor.
 *
 * @author JavaSaBr
 */
public class SceneLayerModelNode extends NodeModelNode<SceneLayer> implements Hideable {

    public SceneLayerModelNode(@NotNull final SceneLayer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {
        super.fillContextMenu(nodeTree, items);

        final SceneLayer layer = getElement();

        if (!layer.isBuiltIn()) {
            items.add(new RemoveSceneLayerAction(nodeTree, this));
        }
    }

    @Nullable
    @Override
    protected Menu createToolMenu(final @NotNull ModelNodeTree nodeTree) {
        return null;
    }

    @Override
    public boolean canEditName() {
        return !getElement().isBuiltIn();
    }

    @Override
    protected boolean canRemove() {
        return false;
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
    public void show() {
        getElement().show();
    }

    @Override
    public void hide() {
        getElement().hide();
    }
}
