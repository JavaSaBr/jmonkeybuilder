package com.ss.editor.ui.control.model.tree.node;

import com.jme3.scene.Mesh;
import com.ss.editor.ui.Icons;

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

    @Override
    public Image getIcon() {
        return Icons.MESH_16;
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
