package com.ss.builder.ui.control.tree.node.impl.spatial.particle.emitter.influencer;

import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
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

    public ParticleInfluencerTreeNode(@NotNull final ParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final ParticleInfluencer element = getElement();
        return element.getClass().getSimpleName();
    }
}
