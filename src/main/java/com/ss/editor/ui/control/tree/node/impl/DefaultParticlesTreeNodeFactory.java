package com.ss.editor.ui.control.tree.node.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.jme3.effect.shapes.*;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.ParticleEmitterTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.DefaultParticleInfluencerTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.EmptyParticleInfluencerTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.ParticleInfluencerTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.RadialParticleInfluencerTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape.*;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of a tree node factory to make default particle nodes.
 *
 * @author JavaSaBr
 */
public class DefaultParticlesTreeNodeFactory implements TreeNodeFactory {

    @Override
    @Nullable
    public <T, V extends TreeNode<T>> V createFor(@Nullable final T element, final long objectId) {

        if (element instanceof ParticleEmitter) {
            return unsafeCast(new ParticleEmitterTreeNode((ParticleEmitter) element, objectId));
        } else if (element instanceof EmitterShape) {

            if (element instanceof EmitterBoxShape) {
                return unsafeCast(new EmitterBoxShapeTreeNode((EmitterBoxShape) element, objectId));
            } else if (element instanceof EmitterSphereShape) {
                return unsafeCast(new EmitterSphereShapeTreeNode((EmitterSphereShape) element, objectId));
            } else if (element instanceof EmitterPointShape) {
                return unsafeCast(new EmitterPointShapeTreeNode((EmitterPointShape) element, objectId));
            } else if (element instanceof EmitterMeshConvexHullShape) {
                return unsafeCast(new EmitterMeshConvexHullShapeTreeNode((EmitterMeshConvexHullShape) element, objectId));
            } else if (element instanceof EmitterMeshFaceShape) {
                return unsafeCast(new EmitterMeshFaceShapeTreeNode((EmitterMeshFaceShape) element, objectId));
            } else if (element instanceof EmitterMeshVertexShape) {
                return unsafeCast(new EmitterMeshVertexShapeTreeNode((EmitterMeshVertexShape) element, objectId));
            }

            return unsafeCast(new EmitterShapeTreeNode((EmitterShape) element, objectId));

        } else if (element instanceof ParticleInfluencer) {

            if (element instanceof EmptyParticleInfluencer) {
                return unsafeCast(new EmptyParticleInfluencerTreeNode((EmptyParticleInfluencer) element, objectId));
            } else if (element instanceof RadialParticleInfluencer) {
                return unsafeCast(new RadialParticleInfluencerTreeNode((RadialParticleInfluencer) element, objectId));
            } else if (element instanceof DefaultParticleInfluencer) {
                return unsafeCast(new DefaultParticleInfluencerTreeNode((DefaultParticleInfluencer) element, objectId));
            }

            return unsafeCast(new ParticleInfluencerTreeNode((ParticleInfluencer) element, objectId));
        }

        return null;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
