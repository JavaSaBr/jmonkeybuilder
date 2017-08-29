package com.ss.editor.ui.control.material.tree.node;

import com.ss.editor.model.node.material.MaterialSettings;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base presentation of material settings in the {@link com.ss.editor.ui.control.tree.NodeTree}.
 *
 * @param <T> the type of material settings.
 * @author JavaSaBr
 */
public class MaterialSettingsTreeNode<T extends MaterialSettings> extends TreeNode<T> {

    public MaterialSettingsTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }
}
