package com.ss.editor.ui.control.model.node;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

/**
 * The implementation of the {@link TreeNode} to represent the {@link Buffer} in the editor.
 *
 * @author JavaSaBr
 */
public class BufferTreeNode extends TreeNode<Buffer> {

    public BufferTreeNode(@NotNull final Buffer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public @Nullable Image getIcon() {
        return Icons.DATA_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return getElement().getClass().getSimpleName();
    }
}
