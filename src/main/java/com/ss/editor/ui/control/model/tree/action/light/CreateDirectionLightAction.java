package com.ss.editor.ui.control.model.tree.action.light;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
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
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.SUN_16;
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT;
    }

    @Override
    @FXThread
    protected @NotNull Light createLight() {
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setName(Messages.MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT);
        return directionalLight;
    }
}
