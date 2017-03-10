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
 * @author vp-byte
 */
public class AssetLinkNodeModelNode extends NodeModelNode<AssetLinkNode> {

    public AssetLinkNodeModelNode(@NotNull AssetLinkNode element, long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.LINKNODE_16;
    }

}
