package com.ss.editor.ui.control.model.node;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.scene.VertexBuffer;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

import java.nio.Buffer;

/**
 * The implementation of the {@link ModelNode} to represent the {@link VertexBuffer} in the editor.
 *
 * @author JavaSaBr
 */
public class VertexBufferModelNode extends ModelNode<VertexBuffer> {

    /**
     * Instantiates a new Vertex buffer model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public VertexBufferModelNode(@NotNull final VertexBuffer element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.VERTEX_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_VERTEX_BUFFER + " [" + getElement().getBufferType() + "]";
    }

    @Override
    public boolean hasChildren(@NotNull final AbstractNodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final VertexBuffer vertexBuffer = getElement();

        final Buffer data = vertexBuffer.getData();
        if (data == null) return EMPTY_ARRAY;

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        result.add(createFor(data));

        return result;
    }
}
