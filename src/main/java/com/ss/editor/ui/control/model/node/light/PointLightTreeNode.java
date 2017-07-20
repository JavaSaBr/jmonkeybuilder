package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.PointLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.util.StringUtils;

/**
 * The implementation of {@link LightTreeNode} to present point lights.
 *
 * @author JavaSaBr
 */
public class PointLightTreeNode extends LightTreeNode<PointLight> {

    /**
     * Instantiates a new Point light model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public PointLightTreeNode(@NotNull final PointLight element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.POINT_LIGHT_16;
    }

    @NotNull
    @Override
    public String getName() {
        final PointLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_POINT_LIGHT : name;
    }
}
