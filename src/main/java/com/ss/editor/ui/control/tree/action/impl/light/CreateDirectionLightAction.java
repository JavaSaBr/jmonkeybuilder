package com.ss.editor.ui.control.tree.action.impl.light;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link DirectionalLight}.
 *
 * @author JavaSaBr
 */
public class CreateDirectionLightAction extends AbstractCreateLightAction {

    public CreateDirectionLightAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.SUN_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT;
    }

    @Override
    @FxThread
    protected @NotNull Light createLight() {
        return new DirectionalLight();
    }
}
