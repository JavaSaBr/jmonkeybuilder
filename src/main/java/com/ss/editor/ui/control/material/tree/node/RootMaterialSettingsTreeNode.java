package com.ss.editor.ui.control.material.tree.node;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.node.material.*;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The base presentation of root settings of a material in the {@link com.ss.editor.ui.control.tree.NodeTree}.
 *
 * @author JavaSaBr
 */
public class RootMaterialSettingsTreeNode extends MaterialSettingsTreeNode<RootMaterialSettings> {

    public RootMaterialSettingsTreeNode(@NotNull final RootMaterialSettings element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final RootMaterialSettings settings = getElement();

        final Array<TreeNode<?>> children = ArrayFactory.newArray(TreeNode.class);
        children.add(FACTORY_REGISTRY.createFor(new TexturesSettings(settings.getMaterial())));
        children.add(FACTORY_REGISTRY.createFor(new ColorsSettings(settings.getMaterial())));
        children.add(FACTORY_REGISTRY.createFor(new RenderSettings(settings.getMaterial())));
        children.add(FACTORY_REGISTRY.createFor(new OtherSettings(settings.getMaterial())));

        return children;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MATERIAL_SETTINGS_MAIN;
    }

    @Override
    @FXThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }
}
