package com.ss.builder.fx.control.tree.node.impl.spatial;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.IntMap;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
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

    public MeshTreeNode(@NotNull Mesh element, long objectId) {
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
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull NodeTree<?> nodeTree) {

        var element = getElement();
        var buffers = element.getBuffers();
        var result = ArrayFactory.<TreeNode<?>>newArray(TreeNode.class, buffers.size());

        buffers.forEach(entry -> result.add(FACTORY_REGISTRY.createFor(entry.getValue())));

        return result;
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull NodeTree<?> nodeTree) {
        return true;
    }

    @Override
    @FxThread
    public boolean canCopy() {
        return true;
    }
}
