package com.ss.builder.ui.control.tree.node.impl.scene;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.TreeNode;
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
