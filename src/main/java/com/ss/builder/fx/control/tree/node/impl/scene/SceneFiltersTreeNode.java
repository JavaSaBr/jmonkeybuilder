package com.ss.builder.fx.control.tree.node.impl.scene;

import com.jme3.util.SafeArrayList;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.scene.SceneFiltersNode;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.scene.SceneNodeTree;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.builder.model.scene.SceneFiltersNode;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.scene.SceneNodeTree;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present scene filters node.
 *
 * @author JavaSaBr
 */
public class SceneFiltersTreeNode extends TreeNode<SceneFiltersNode> {

    public SceneFiltersTreeNode(@NotNull final SceneFiltersNode element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.FILTER_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.SCENE_FILE_EDITOR_NODE_FILTERS;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return nodeTree instanceof SceneNodeTree;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        if (!(nodeTree instanceof SceneNodeTree)) {
            return super.getChildren(nodeTree);
        }

        final SafeArrayList<SceneFilter> filters = getElement().getFilters();

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        filters.forEach(sceneFilter -> result.add(FACTORY_REGISTRY.createFor(sceneFilter.get())));

        return result;
    }
}
