package com.ss.builder.fx.control.tree.node.impl.light;

import com.jme3.light.DirectionalLight;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.Icons;
import com.ss.rlib.common.util.StringUtils;
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
    @FxThread
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
