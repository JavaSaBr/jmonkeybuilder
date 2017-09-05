package com.ss.editor.ui.control.material.tree.node;

import com.ss.editor.Messages;
import com.ss.editor.model.node.material.TexturesSettings;
import org.jetbrains.annotations.NotNull;

/**
 * The base presentation of textures settings of a material in the {@link com.ss.editor.ui.control.tree.NodeTree}.
 *
 * @author JavaSaBr
 */
public class TexturesSettingsTreeNode extends MaterialSettingsTreeNode<TexturesSettings> {

    public TexturesSettingsTreeNode(@NotNull final TexturesSettings element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public @NotNull String getName() {
        return Messages.MATERIAL_SETTINGS_TEXTURES;
    }
}
