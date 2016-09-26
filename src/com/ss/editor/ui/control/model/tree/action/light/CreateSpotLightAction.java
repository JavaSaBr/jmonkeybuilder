package com.ss.editor.ui.control.model.tree.action.light;

import com.jme3.light.Light;
import com.jme3.light.SpotLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

/**
 * The action for creating the {@link SpotLight}.
 *
 * @author JavaSaBr
 */
public class CreateSpotLightAction extends AbstractCreateLightAction {

    public CreateSpotLightAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
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
