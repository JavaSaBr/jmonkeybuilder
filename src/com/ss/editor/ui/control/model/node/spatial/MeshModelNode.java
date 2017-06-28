package com.ss.editor.ui.control.model.node.spatial;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.IntMap;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelNode} to represent the {@link Mesh} in the editor.
 *
 * @author JavaSaBr
 */
public class MeshModelNode extends ModelNode<Mesh> {

    /**
     * Instantiates a new Mesh model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public MeshModelNode(@NotNull final Mesh element, final long objectId) {
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
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);

        final Mesh element = getElement();
        final IntMap<VertexBuffer> buffers = element.getBuffers();
        buffers.forEach(entry -> result.add(createFor(entry.getValue())));

        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return true;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return true;
    }
}
