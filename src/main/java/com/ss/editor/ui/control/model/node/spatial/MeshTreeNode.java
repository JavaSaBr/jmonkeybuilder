package com.ss.editor.ui.control.model.node.spatial;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.IntMap;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link TreeNode} to represent the {@link Mesh} in the editor.
 *
 * @author JavaSaBr
 */
public class MeshTreeNode extends TreeNode<Mesh> {

    /**
     * Instantiates a new Mesh model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public MeshTreeNode(@NotNull final Mesh element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.MESH_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MESH;
    }

    @NotNull
    @Override
    public Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);

        final Mesh element = getElement();
        final IntMap<VertexBuffer> buffers = element.getBuffers();
        buffers.forEach(entry -> result.add(FACTORY_REGISTRY.createFor(entry.getValue())));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }

    @Override
    public boolean canCopy() {
        return true;
    }
}
