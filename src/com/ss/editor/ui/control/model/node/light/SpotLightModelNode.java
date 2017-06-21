package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.SpotLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.util.StringUtils;

/**
 * The implementation of {@link LightModelNode} to present spot lights.
 *
 * @author JavaSaBr
 */
public class SpotLightModelNode extends LightModelNode<SpotLight> {

    /**
     * Instantiates a new Spot light model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public SpotLightModelNode(@NotNull final SpotLight element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.LAMP_16;
    }

    @NotNull
    @Override
    public String getName() {
        final SpotLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_SPOT_LIGHT : name;
    }
}
