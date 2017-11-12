package com.ss.editor.ui.control.material.tree.node;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.node.material.RenderSettings;
import org.jetbrains.annotations.NotNull;

/**
 * The base presentation of render settings of a material in the {@link com.ss.editor.ui.control.tree.NodeTree}.
 *
 * @author JavaSaBr
 */
public class RenderSettingsTreeNode extends MaterialSettingsTreeNode<RenderSettings> {

    public RenderSettingsTreeNode(@NotNull final RenderSettings element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MATERIAL_SETTINGS_RENDER;
    }
}
