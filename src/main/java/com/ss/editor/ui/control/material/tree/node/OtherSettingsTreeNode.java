package com.ss.editor.ui.control.material.tree.node;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.node.material.OtherSettings;
import org.jetbrains.annotations.NotNull;

/**
 * The base presentation of other settings of a material in the {@link com.ss.editor.ui.control.tree.NodeTree}.
 *
 * @author JavaSaBr
 */
public class OtherSettingsTreeNode extends MaterialSettingsTreeNode<OtherSettings> {

    public OtherSettingsTreeNode(@NotNull final OtherSettings element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.MATERIAL_SETTINGS_OTHER;
    }
}
