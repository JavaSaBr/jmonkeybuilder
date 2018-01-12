package com.ss.editor.ui.control.model.tree.action.control.physics;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.control.AbstractCreateControlAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link CharacterControl}.
 *
 * @author JavaSaBr
 */
public class CreateCharacterControlAction extends AbstractCreateControlAction {

    public CreateCharacterControlAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.CHARACTER_16;
    }


    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_CHARACTER;
    }

    @Override
    @FxThread
    protected @NotNull Control createControl(@NotNull final Spatial parent) {
        return new CharacterControl(new CapsuleCollisionShape(0.6f, 1.8f), 0.03f);
    }
}
