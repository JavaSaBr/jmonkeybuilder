package com.ss.editor.ui.control.model.tree.action.light;

import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action for creating the {@link AmbientLight}.
 *
 * @author JavaSaBr
 */
public class CreateAmbientLightAction extends AbstractCreateLightAction {

    public CreateAmbientLightAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.AMBIENT_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_AMBIENT_LIGHT;
    }

    @NotNull
    @Override
    protected Light createLight() {
        return new AmbientLight();
    }
}
