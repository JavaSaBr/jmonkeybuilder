package com.ss.editor.ui.control.model.node.light;

import com.jme3.light.AmbientLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.util.StringUtils;

/**
 * The implementation of {@link LightModelNode} to present ambient lights.
 *
 * @author JavaSaBr
 */
public class AmbientLightModelNode extends LightModelNode<AmbientLight> {

    /**
     * Instantiates a new Ambient light model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AmbientLightModelNode(@NotNull final AmbientLight element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.AMBIENT_16;
    }

    @NotNull
    @Override
    public String getName() {
        final AmbientLight element = getElement();
        final String name = element.getName();
        return StringUtils.isEmpty(name) ? Messages.MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT : name;
    }
}
