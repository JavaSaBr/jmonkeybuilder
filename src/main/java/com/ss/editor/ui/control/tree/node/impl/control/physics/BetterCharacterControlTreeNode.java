package com.ss.editor.ui.control.tree.node.impl.control.physics;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link CharacterControl}.
 *
 * @author JavaSaBr
 */
public class BetterCharacterControlTreeNode extends PhysicsControlTreeNode<BetterCharacterControl> {

    public BetterCharacterControlTreeNode(@NotNull final BetterCharacterControl element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.CHARACTER_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_CHARACTER_CONTROL;
    }
}
