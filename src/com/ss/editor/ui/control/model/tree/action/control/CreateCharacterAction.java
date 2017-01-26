package com.ss.editor.ui.control.model.tree.action.control;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.control.Control;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link CharacterControl}.
 *
 * @author JavaSaBr
 */
public class CreateCharacterAction extends AbstractCreateControlAction {

    public CreateCharacterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.GEAR_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return "Character";
    }

    @NotNull
    @Override
    protected Control createControl() {
        return new CharacterControl(new CapsuleCollisionShape(0.6f, 1.8f), 0.03f);
    }
}
