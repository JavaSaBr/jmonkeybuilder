package com.ss.editor.ui.control.model.node.spatial;

import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SpatialModelNode} for representing the {@link Node} in the editor.
 *
 * @author vp -byte
 */
public class AssetLinkNodeModelNode extends NodeModelNode<AssetLinkNode> {

    /**
     * Instantiates a new Asset link node model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AssetLinkNodeModelNode(@NotNull final AssetLinkNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.LINKED_NODE_16;
    }
}
