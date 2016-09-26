package com.ss.editor.ui.control.model.tree.node.spatial.emitter;

import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.RemoveParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link ModelNode} for representing the {@link ParticleInfluencer} in
 * the editor.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencerModelNode extends ModelNode<ParticleInfluencer> {

    /**
     * The emitter node.
     */
    @NotNull
    private final ParticleEmitterNode emitterNode;

    public ParticleInfluencerModelNode(@NotNull ParticleInfluencers influencers, @NotNull final ParticleInfluencer element, final long objectId) {
        super(element, objectId);
        this.emitterNode = influencers.getEmitterNode();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RemoveParticleInfluencerAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }

    /**
     * @return thr emitter node.
     */
    @NotNull
    public ParticleEmitterNode getEmitterNode() {
        return emitterNode;
    }

    @NotNull
    @Override
    public String getName() {
        final ParticleInfluencer element = getElement();
        return element.getName();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }
}
