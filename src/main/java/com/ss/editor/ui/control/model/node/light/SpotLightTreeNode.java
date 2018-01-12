package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.SpotLight;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.rlib.util.StringUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of {@link LightTreeNode} to present spot lights.
 *
 * @author JavaSaBr
 */
public class SpotLightTreeNode extends LightTreeNode<SpotLight> {

    public SpotLightTreeNode(@NotNull final SpotLight element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.LAMP_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final SpotLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_SPOT_LIGHT : name;
    }
}
