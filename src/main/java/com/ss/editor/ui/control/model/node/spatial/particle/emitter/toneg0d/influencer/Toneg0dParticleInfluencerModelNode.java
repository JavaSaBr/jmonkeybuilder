package com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.influerencer.RemoveParticleInfluencerAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link ModelNode} for representing the {@link ParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class Toneg0dParticleInfluencerModelNode extends ModelNode<ParticleInfluencer> {

    /**
     * Instantiates a new Particle influencer model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public Toneg0dParticleInfluencerModelNode(@NotNull final ParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RemoveParticleInfluencerAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }

    @NotNull
    @Override
    public String getName() {
        final ParticleInfluencer element = getElement();
        return element.getName();
    }
}
