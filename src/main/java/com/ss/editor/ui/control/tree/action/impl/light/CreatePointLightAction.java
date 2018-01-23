package com.ss.editor.ui.control.tree.action.impl.light;

import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link PointLight}.
 *
 * @author JavaSaBr
 */
public class CreatePointLightAction extends AbstractCreateLightAction {

    public CreatePointLightAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.POINT_LIGHT_16;
    }


    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_POINT_LIGHT;
    }

    @Override
    @FxThread
    protected @NotNull Light createLight() {
        final PointLight pointLight = new PointLight();
        pointLight.setName(Messages.MODEL_NODE_TREE_ACTION_POINT_LIGHT);
        return pointLight;
    }
}
