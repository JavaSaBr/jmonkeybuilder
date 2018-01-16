package com.ss.editor.ui.control.model.node.spatial;

import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SpatialTreeNode} for representing the {@link Node} in the editor.
 *
 * @author vp -byte
 */
public class AssetLinkNodeTreeNode extends NodeTreeNode<AssetLinkNode> {

    public AssetLinkNodeTreeNode(@NotNull final AssetLinkNode element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.LINKED_NODE_16;
    }
}
