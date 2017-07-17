package com.ss.editor.ui.control.model.node;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

/**
 * The implementation of the {@link ModelNode} to represent the {@link Buffer} in the editor.
 *
 * @author JavaSaBr
 */
public class BufferModelNode extends ModelNode<Buffer> {

    /**
     * Instantiates a new Buffer model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public BufferModelNode(@NotNull final Buffer element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.DATA_16;
    }

    @NotNull
    @Override
    public String getName() {
        return getElement().getClass().getSimpleName();
    }
}
