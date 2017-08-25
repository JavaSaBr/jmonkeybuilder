package com.ss.editor.ui.control.model.node.spatial;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.material.Material;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.SaveAsMaterialAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.util.NodeUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} to represent the {@link Material} in the editor.
 *
 * @author JavaSaBr
 */
public class MaterialTreeNode extends TreeNode<Material> {

    public MaterialTreeNode(@NotNull final Material element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public @Nullable Image getIcon() {
        return Icons.MATERIAL_16;
    }

    @Override
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MATERIAL;
    }

    @Override
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        super.fillContextMenu(nodeTree, items);

        final TreeNode<?> parent = notNull(getParent());
        final Object parentElement = parent.getElement();
        final Object linkNode = parentElement instanceof Spatial ?
                NodeUtils.findParent((Spatial) parentElement, AssetLinkNode.class::isInstance) : null;

        if (linkNode == null) {
            items.add(new SaveAsMaterialAction(nodeTree, this));
        }
    }

    @Override
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return false;
    }

    @Override
    public boolean canCopy() {
        return true;
    }
}
