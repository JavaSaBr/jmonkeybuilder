package com.ss.builder.fx.control.tree.node.impl.material.settings;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.node.material.TexturesSettings;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.node.material.TexturesSettings;
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
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MATERIAL_SETTINGS_TEXTURES;
    }
}
