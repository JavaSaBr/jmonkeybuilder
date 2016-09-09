package com.ss.editor.ui.control.model.tree.node.light;

import com.jme3.light.AmbientLight;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * Реализация узла с амбиент светом.
 *
 * @author Ronn
 */
public class AmbientLightModelNode extends LightModelNode<AmbientLight> {

    public AmbientLightModelNode(final AmbientLight element, final long objectId) {
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
        return Messages.MODEL_FILE_EDITOR_NODE_AMBIENT_LIGHT;
    }
}
