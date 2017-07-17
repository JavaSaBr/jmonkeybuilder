package com.ss.editor.ui.control.model.node.spatial.scene;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.RenameNodeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.model.node.spatial.NodeModelNode;
import com.ss.editor.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link SceneNode} in the editor.
 *
 * @author JavaSaBr
 */
public class SceneNodeModelNode extends NodeModelNode<SceneNode> {

    /**
     * Instantiates a new Scene node model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public SceneNodeModelNode(@NotNull final SceneNode element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        if (!(nodeTree instanceof ModelNodeTree)) return;

        final Menu createMenu = createCreationMenu(nodeTree);

        items.add(createMenu);
        items.add(new RenameNodeAction(nodeTree, this));
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.SCENE_16;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canAccept(@NotNull final ModelNode<?> child) {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }
}
