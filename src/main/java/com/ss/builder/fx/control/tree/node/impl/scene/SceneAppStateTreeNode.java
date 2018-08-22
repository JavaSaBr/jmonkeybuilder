package com.ss.builder.fx.control.tree.node.impl.scene;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present {@link SceneAppState} in the scene model tree.
 *
 * @author JavaSaBr
 */
public class SceneAppStateTreeNode extends TreeNode<SceneAppState> {

    public SceneAppStateTreeNode(@NotNull final SceneAppState element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return getElement().getName();
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.SETTINGS_16;
    }
}
