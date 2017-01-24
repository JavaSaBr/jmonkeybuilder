package com.ss.editor.ui.control.tree.node;

import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of the {@link ModelNode} for representing the {@link Mesh} in the editor.
 *
 * @author JavaSaBr
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

    @Override
    public boolean hasChildren() {
        return false;
    }
}
