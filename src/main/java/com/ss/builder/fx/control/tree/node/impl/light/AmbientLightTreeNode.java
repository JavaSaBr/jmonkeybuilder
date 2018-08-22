package com.ss.builder.fx.control.tree.node.impl.light;

import com.jme3.light.AmbientLight;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.common.util.StringUtils;

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
    @FxThread
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
