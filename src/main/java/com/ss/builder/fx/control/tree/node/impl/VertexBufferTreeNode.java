package com.ss.builder.ui.control.tree.node.impl;

import com.jme3.scene.VertexBuffer;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.control.model.ModelNodeTree;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.ObjectUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

import static com.ss.rlib.common.util.ObjectUtils.notNull;

/**
 * The implementation of the {@link TreeNode} to represent the {@link VertexBuffer} in the editor.
 *
 * @author JavaSaBr
 */
public class VertexBufferTreeNode extends TreeNode<VertexBuffer> {

    public VertexBufferTreeNode(@NotNull VertexBuffer element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.VERTEX_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_VERTEX_BUFFER + " [" + getElement().getBufferType() + "]";
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull NodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull NodeTree<?> nodeTree) {

        var vertexBuffer = getElement();
        var data = vertexBuffer.getData();

        if (data == null) {
            return Array.empty();
        }

        return Array.of(notNull(FACTORY_REGISTRY.createFor(data)));
    }
}
