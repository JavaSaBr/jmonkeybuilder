package com.ss.editor.ui.control.model.tree.node.light;

import com.jme3.light.DirectionalLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import javafx.scene.image.Image;

/**
 * Реализация узла с направленным светом.
 *
 * @author Ronn
 */
public class DirectionalLightModelNode extends LightModelNode<DirectionalLight> {

    public DirectionalLightModelNode(final DirectionalLight element, final long objectId) {
        super(element, objectId);
    }

    @Override
    public Image getIcon() {
        return Icons.SUN_16;
    }

    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_DIRECTION_LIGHT;
    }
}
