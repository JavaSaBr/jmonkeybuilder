package com.ss.editor.ui.control.tree.node.impl.spatial;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.IntMap;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;

/**
 * The implementation of the {@link TreeNode} to represent the {@link Mesh} in the editor.
 *
 * @author JavaSaBr
 */
public class MeshTreeNode extends TreeNode<Mesh> {

    public MeshTreeNode(@NotNull final Mesh element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.MESH_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MESH;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);

        final Mesh element = getElement();
        final IntMap<VertexBuffer> buffers = element.getBuffers();
        buffers.forEach(entry -> result.add(FACTORY_REGISTRY.createFor(entry.getValue())));

        return result;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }

    @Override
    @FxThread
    public boolean canCopy() {
        return true;
    }
}
