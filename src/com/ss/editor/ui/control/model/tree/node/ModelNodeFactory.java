package com.ss.editor.ui.control.model.tree.node;

import static rlib.util.ClassUtils.unsafeCast;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.AudioTrack;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.EffectTrack;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.control.model.tree.node.control.ControlModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationAudioTrackModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationBoneTrackModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationEffectTrackModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationModelNode;
import com.ss.editor.ui.control.model.tree.node.light.AmbientLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.DirectionalLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.LightProbeModelNode;
import com.ss.editor.ui.control.model.tree.node.light.PointLightModelNode;
import com.ss.editor.ui.control.model.tree.node.light.SpotLightModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.GeometryModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.NodeModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.ParticleNodeModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.emitter.ParticleEmitterMeshModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.emitter.ParticleEmitterNodeModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.emitter.ParticleGeometryModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.emitter.ParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.emitter.ParticleInfluencersModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicLong;

import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.node.ParticleNode;

/**
 * The factory for creating the {@link ModelNode} of the element of {@link com.jme3.scene.Spatial}.
 *
 * @author JavaSabr
 */
public class ModelNodeFactory {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @NotNull
    public static <T, V extends ModelNode<T>> V createFor(@NotNull final T element) {
        return createFor(element, null);
    }

    @NotNull
    public static <T, V extends ModelNode<T>> V createFor(@NotNull final T element, @Nullable Object first) {

        if (element instanceof Animation) {
            return unsafeCast(new AnimationModelNode((Animation) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof BoneTrack) {
            return unsafeCast(new AnimationBoneTrackModelNode((BoneTrack) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof EffectTrack) {
            return unsafeCast(new AnimationEffectTrackModelNode((EffectTrack) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof AudioTrack) {
            return unsafeCast(new AnimationAudioTrackModelNode((AudioTrack) element, ID_GENERATOR.incrementAndGet()));
        }

        if (element instanceof AnimControl) {
            return unsafeCast(new AnimationControlModelNode((AnimControl) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof Control) {
            return unsafeCast(new ControlModelNode<>((Control) element, ID_GENERATOR.incrementAndGet()));
        }

        if (element instanceof LightProbe) {
            return unsafeCast(new LightProbeModelNode((LightProbe) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof AmbientLight) {
            return unsafeCast(new AmbientLightModelNode((AmbientLight) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof DirectionalLight) {
            return unsafeCast(new DirectionalLightModelNode((DirectionalLight) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof SpotLight) {
            return unsafeCast(new SpotLightModelNode((SpotLight) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof PointLight) {
            return unsafeCast(new PointLightModelNode((PointLight) element, ID_GENERATOR.incrementAndGet()));
        }

        if (element instanceof ParticleEmitterNode) {
            return unsafeCast(new ParticleEmitterNodeModelNode((ParticleEmitterNode) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof ParticleInfluencers) {
            return unsafeCast(new ParticleInfluencersModelNode((ParticleInfluencers) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof ParticleInfluencer && first instanceof ParticleInfluencers) {
            return unsafeCast(new ParticleInfluencerModelNode((ParticleInfluencers) first, (ParticleInfluencer) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof EmitterMesh) {
            return unsafeCast(new ParticleEmitterMeshModelNode((EmitterMesh) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof ParticleNode) {
            return unsafeCast(new ParticleNodeModelNode((ParticleNode) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof ParticleGeometry) {
            return unsafeCast(new ParticleGeometryModelNode((ParticleGeometry) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof Mesh) {
            return unsafeCast(new MeshModelNode((Mesh) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof Geometry) {
            return unsafeCast(new GeometryModelNode<>((Geometry) element, ID_GENERATOR.incrementAndGet()));
        } else if (element instanceof Node) {
            return unsafeCast(new NodeModelNode<>((Node) element, ID_GENERATOR.incrementAndGet()));
        }

        throw new IllegalArgumentException("unknown " + element);
    }
}
