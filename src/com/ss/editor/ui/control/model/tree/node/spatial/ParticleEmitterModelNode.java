package com.ss.editor.ui.control.model.tree.node.spatial;

import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link NodeModelNode} for representing the {@link ParticleEmitterNode}
 * in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterModelNode extends NodeModelNode<ParticleEmitterNode> {

    public ParticleEmitterModelNode(final ParticleEmitterNode element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }
}
