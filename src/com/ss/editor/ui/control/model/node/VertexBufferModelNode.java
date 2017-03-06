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
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelNode} to represent the {@link VertexBuffer} in the editor.
 *
 * @author JavaSaBr
 */
public class VertexBufferModelNode extends ModelNode<VertexBuffer> {

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

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        result.add(createFor(vertexBuffer.getData()));

        return result;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }
}
