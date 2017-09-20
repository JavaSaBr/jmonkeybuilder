package com.ss.editor.ui.control.model.node;

import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

/**
 * The implementation of the {@link TreeNode} to represent the {@link VertexBuffer} in the editor.
 *
 * @author JavaSaBr
 */
public class VertexBufferTreeNode extends TreeNode<VertexBuffer> {

    public VertexBufferTreeNode(@NotNull final VertexBuffer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public @Nullable Image getIcon() {
        return Icons.VERTEX_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_VERTEX_BUFFER + " [" + getElement().getBufferType() + "]";
    }

    @Override
    @FXThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }

    @Override
    @FXThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final VertexBuffer vertexBuffer = getElement();

        final Buffer data = vertexBuffer.getData();
        if (data == null) return EMPTY_ARRAY;

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        result.add(FACTORY_REGISTRY.createFor(data));

        return result;
    }
}
