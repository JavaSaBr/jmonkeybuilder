package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.AmbientLight;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.util.StringUtils;

/**
 * The implementation of {@link LightTreeNode} to present ambient lights.
 *
 * @author JavaSaBr
 */
public class AmbientLightTreeNode extends LightTreeNode<AmbientLight> {

    public AmbientLightTreeNode(@NotNull final AmbientLight element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public @Nullable Image getIcon() {
        return Icons.AMBIENT_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final AmbientLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT : name;
    }
}
