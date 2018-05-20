package com.ss.editor.ui.control.tree.node.impl.scene;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.scene.SceneFiltersNode;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.scene.SceneNodeTree;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.impl.RenameNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.impl.spatial.NodeTreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link NodeTreeNode} for representing the {@link SceneNode} in the editor.
 *
 * @author JavaSaBr
 */
public class SceneNodeTreeNode extends NodeTreeNode<SceneNode> {

    public SceneNodeTreeNode(@NotNull final SceneNode element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {

        if (!(nodeTree instanceof ModelNodeTree)) {
            return;
        }

        final Menu createMenu = createCreationMenu(nodeTree);

        items.add(createMenu);
        items.add(new RenameNodeAction(nodeTree, this));
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return super.hasChildren(nodeTree) || nodeTree instanceof SceneNodeTree;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        if (nodeTree instanceof ModelNodeTree) {
            return super.getChildren(nodeTree);
        } else if (!(nodeTree instanceof SceneNodeTree)) {
            return EMPTY_ARRAY;
        }

        final SceneNode sceneNode = getElement();

        //TODO to add other children
        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        result.add(FACTORY_REGISTRY.createFor(new SceneFiltersNode(sceneNode)));

        return result;
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.SCENE_16;
    }

    @Override
    @FxThread
    public boolean canMove() {
        return false;
    }

    @Override
    @FxThread
    public boolean canAccept(@NotNull final TreeNode<?> treeNode, final boolean isCopy) {
        return false;
    }

    @Override
    @FxThread
    public boolean canCopy() {
        return false;
    }
}
