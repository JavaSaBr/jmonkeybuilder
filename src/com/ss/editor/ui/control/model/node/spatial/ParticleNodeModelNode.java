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

    /**
     * Instantiates a new Particle node model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ParticleNodeModelNode(final ParticleNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }

    @Override
    public boolean canRemove() {
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
