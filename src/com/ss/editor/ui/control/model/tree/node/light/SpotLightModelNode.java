package com.ss.editor.ui.control.model.tree.node.light;

import com.jme3.light.SpotLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import javafx.scene.image.Image;

/**
 * Реализация узла для света в стиле фанарика.
 *
 * @author Ronn
 */
public class SpotLightModelNode extends LightModelNode<SpotLight> {

    public SpotLightModelNode(final SpotLight element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public Image getIcon() {
        return Icons.LAMP_16;
    }

    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_SPOT_LIGHT;
    }
}
