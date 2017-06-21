package com.ss.editor.ui.control.model.tree.action.control.physics;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link CharacterControl}.
 *
 * @author JavaSaBr
 */
public class CreateCharacterControlAction extends AbstractCreateControlAction {

    /**
     * Instantiates a new Create character control action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateCharacterControlAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.CHARACTER_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_CHARACTER;
    }

    @NotNull
    @Override
    protected Control createControl(@NotNull final Spatial parent) {
        return new CharacterControl(new CapsuleCollisionShape(0.6f, 1.8f), 0.03f);
    }
}
