package com.ss.editor.ui.control.tree.node.impl.scene;

import com.jme3.util.SafeArrayList;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.model.scene.SceneAppStatesNode;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.scene.SceneNodeTree;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present scene app states node.
 *
 * @author JavaSaBr
 */
public class SceneAppStatesTreeNode extends TreeNode<SceneAppStatesNode> {

    public SceneAppStatesTreeNode(@NotNull final SceneAppStatesNode element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.SETTINGS_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.SCENE_FILE_EDITOR_TOOL_APP_STATES;
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

        final @NotNull SafeArrayList<SceneAppState> appStates = getElement().getAppStates();

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        appStates.forEach(appState -> result.add(FACTORY_REGISTRY.createFor(appState)));

        return result;
    }
}
