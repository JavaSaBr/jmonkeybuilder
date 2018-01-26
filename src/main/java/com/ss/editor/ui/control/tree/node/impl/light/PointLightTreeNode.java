package com.ss.editor.ui.control.tree.node.impl.light;

import com.jme3.light.PointLight;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.rlib.util.StringUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of {@link LightTreeNode} to present point lights.
 *
 * @author JavaSaBr
 */
public class PointLightTreeNode extends LightTreeNode<PointLight> {

    public PointLightTreeNode(@NotNull final PointLight element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.POINT_LIGHT_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final PointLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_POINT_LIGHT : name;
    }
}
