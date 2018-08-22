package com.ss.builder.ui.control.tree.action.impl.light;

import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
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
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.AMBIENT_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT;
    }

    @Override
    @FxThread
    protected @NotNull Light createLight() {
        final AmbientLight ambientLight = new AmbientLight();
        ambientLight.setName(Messages.MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT);
        return ambientLight;
    }
}
