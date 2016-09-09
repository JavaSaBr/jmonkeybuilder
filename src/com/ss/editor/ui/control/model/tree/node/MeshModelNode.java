package com.ss.editor.ui.control.model.tree.node;

import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * Реализация узла представляющего мешь геометрии.
 *
 * @author Ronn
 */
public class MeshModelNode extends ModelNode<Mesh> {

    public MeshModelNode(final Mesh element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.MESH_16;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_MESH;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return true;
    }
}
