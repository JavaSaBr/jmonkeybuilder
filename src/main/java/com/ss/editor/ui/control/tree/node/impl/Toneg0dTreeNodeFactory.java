package com.ss.editor.ui.control.tree.node.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.node.particles.Toneg0dParticleInfluencers;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.Toneg0DParticleEmitterNodeTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer.Toneg0DParticleInfluencerTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer.Toneg0DParticleInfluencersTreeNode;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of a tree node factory to make toneg0d nodes.
 *
 * @author JavaSaBr
 */
public class Toneg0dTreeNodeFactory implements TreeNodeFactory {

    @Override
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof ParticleEmitterNode) {
            return unsafeCast(new Toneg0DParticleEmitterNodeTreeNode((ParticleEmitterNode) element, objectId));
        } else if (element instanceof Toneg0dParticleInfluencers) {
            return unsafeCast(new Toneg0DParticleInfluencersTreeNode((Toneg0dParticleInfluencers) element, objectId));
        } else if (element instanceof ParticleInfluencer) {
            return unsafeCast(new Toneg0DParticleInfluencerTreeNode((ParticleInfluencer) element, objectId));
        }

        return null;
    }

    @Override
    @FxThread
    public int getOrder() {
        return 1;
    }
}
