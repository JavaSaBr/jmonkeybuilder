package com.ss.editor.ui.control.model.node.control.physics;

import com.jme3.bullet.control.CharacterControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
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
public class CharacterControlTreeNode extends PhysicsObjectTreeNode<CharacterControl> {

    public CharacterControlTreeNode(@NotNull final CharacterControl element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public @Nullable Image getIcon() {
        return Icons.CHARACTER_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_CHARACTER_CONTROL;
    }
}
