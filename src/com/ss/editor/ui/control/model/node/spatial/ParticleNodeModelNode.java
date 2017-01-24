package com.ss.editor.ui.control.model.node.spatial;

import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import tonegod.emitter.node.ParticleNode;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link ParticleNode} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleNodeModelNode extends NodeModelNode<ParticleNode> {

    public ParticleNodeModelNode(final ParticleNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }

    @Override
    protected boolean canRemove() {
        return false;
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean canEditName() {
        return false;
    }
}
