package com.ss.editor.ui.control.model.tree.node.spatial;

import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link ModelNode} for representing the {@link ParticleInfluencer} in
 * the editor.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencerModelNode extends ModelNode<ParticleInfluencer> {

    public ParticleInfluencerModelNode(@NotNull final ParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        final ParticleInfluencer element = getElement();
        return element.getClass().getSimpleName();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }
}
