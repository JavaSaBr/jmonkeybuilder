package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.DirectionalLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.util.StringUtils;

/**
 * The implementation of {@link LightModelNode} to present direction lights.
 *
 * @author JavaSaBr
 */
public class DirectionalLightModelNode extends LightModelNode<DirectionalLight> {

    /**
     * Instantiates a new Directional light model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public DirectionalLightModelNode(@NotNull final DirectionalLight element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.SUN_16;
    }

    @NotNull
    @Override
    public String getName() {
        final DirectionalLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT : name;
    }
}
