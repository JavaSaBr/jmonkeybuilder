package com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link TreeNode} for representing the {@link ParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencerTreeNode extends TreeNode<ParticleInfluencer> {

    /**
     * Instantiates a new Particle influencer model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ParticleInfluencerTreeNode(@NotNull final ParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @NotNull
    @Override
    public String getName() {
        final ParticleInfluencer element = getElement();
        return element.getClass().getSimpleName();
    }
}
