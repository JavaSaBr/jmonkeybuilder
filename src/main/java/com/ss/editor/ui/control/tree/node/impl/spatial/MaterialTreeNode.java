package com.ss.editor.ui.control.tree.node.impl.spatial;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.material.Material;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.impl.MakeAsEmbeddedMaterialAction;
import com.ss.editor.ui.control.tree.action.impl.SaveAsMaterialAction;
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

    public MaterialTreeNode(@NotNull Material element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.MATERIAL_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MATERIAL;
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {
        super.fillContextMenu(nodeTree, items);

        var material = getElement();
        var parent = notNull(getParent());
        var parentElement = parent.getElement();
        var linkNode = parentElement instanceof Spatial ?
                NodeUtils.findParent((Spatial) parentElement, AssetLinkNode.class::isInstance) : null;

        if (linkNode == null) {
            items.add(new SaveAsMaterialAction(nodeTree, this));
        }

        if (material.getKey() != null) {
            items.add(new MakeAsEmbeddedMaterialAction(nodeTree, this));
        }
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull NodeTree<?> nodeTree) {
        return false;
    }

    @Override
    @FxThread
    public boolean canCopy() {
        return true;
    }
}
