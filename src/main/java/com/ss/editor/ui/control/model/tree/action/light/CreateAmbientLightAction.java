package com.ss.editor.ui.control.model.tree.action.light;

import com.jme3.light.AmbientLight;
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
 * The action to create an {@link AmbientLight}.
 *
 * @author JavaSaBr
 */
public class CreateAmbientLightAction extends AbstractCreateLightAction {

    public CreateAmbientLightAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.AMBIENT_16;
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT;
    }

    @Override
    @FXThread
    protected @NotNull Light createLight() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setName(Messages.MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT);
        return ambientLight;
    }
}
