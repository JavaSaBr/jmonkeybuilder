package com.ss.builder.fx.control.tree.node.impl.material.settings;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.node.material.*;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.node.material.*;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.node.TreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
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
    @FxThread
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
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return true;
    }
}
