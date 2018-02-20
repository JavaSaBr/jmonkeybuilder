package com.ss.editor.ui.control.tree.node.impl.scene;

import com.jme3.post.Filter;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The node to present {@link Filter} in the scene model tree.
 *
 * @author JavaSaBr
 */
public class SceneFilterTreeNode extends TreeNode<Filter> {

    public SceneFilterTreeNode(@NotNull final Filter element, final long objectId) {
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
        return Icons.FILTER_16;
    }
}
