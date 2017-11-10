package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.DirectionalLight;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.rlib.util.StringUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of {@link LightTreeNode} to present direction lights.
 *
 * @author JavaSaBr
 */
public class DirectionalLightTreeNode extends LightTreeNode<DirectionalLight> {

    public DirectionalLightTreeNode(@NotNull final DirectionalLight element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public @Nullable Image getIcon() {
        return Icons.SUN_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final DirectionalLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT : name;
    }
}
