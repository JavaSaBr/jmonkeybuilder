package com.ss.editor.ui.control.model.tree.action.light;

import com.jme3.light.Light;
import com.jme3.light.SpotLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link SpotLight}.
 *
 * @author JavaSaBr
 */
public class CreateSpotLightAction extends AbstractCreateLightAction {

    /**
     * Instantiates a new Create spot light action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateSpotLightAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.LAMP_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_SPOT_LIGHT;
    }

    @NotNull
    @Override
    protected Light createLight() {
        return new SpotLight();
    }
}
