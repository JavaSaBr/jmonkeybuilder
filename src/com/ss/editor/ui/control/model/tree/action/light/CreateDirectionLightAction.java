package com.ss.editor.ui.control.model.tree.action.light;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

/**
 * The action for creating the {@link DirectionalLight}.
 *
 * @author JavaSaBr
 */
public class CreateDirectionLightAction extends AbstractCreateLightAction {

    public CreateDirectionLightAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_DIRECTION_LIGHT;
    }

    @NotNull
    @Override
    protected Light createLight() {
        return new DirectionalLight();
    }
}
