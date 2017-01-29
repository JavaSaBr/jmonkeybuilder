package com.ss.editor.ui.control.tree.node;

import static rlib.util.ClassUtils.unsafeCast;
import com.jme3.animation.*;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.light.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.layer.node.LayersRootModelNode;
import com.ss.editor.ui.control.layer.node.SceneLayerModelNode;
import com.ss.editor.ui.control.model.node.control.ControlModelNode;
import com.ss.editor.ui.control.model.node.control.SkeletonControlModelNode;
import com.ss.editor.ui.control.model.node.control.anim.*;
import com.ss.editor.ui.control.model.node.control.physics.CharacterControlModelNode;
import com.ss.editor.ui.control.model.node.control.physics.RagdollControlModelNode;
import com.ss.editor.ui.control.model.node.control.physics.RigidBodyControlModelNode;
import com.ss.editor.ui.control.model.node.control.physics.VehicleControlModelNode;
import com.ss.editor.ui.control.model.node.light.*;
import com.ss.editor.ui.control.model.node.spatial.*;
import com.ss.editor.ui.control.model.node.spatial.emitter.*;
import com.ss.editor.ui.control.model.node.spatial.scene.SceneNodeModelNode;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.node.ParticleNode;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The factory for creating the {@link ModelNode} of the element of {@link com.jme3.scene.Spatial}.
 *
 * @author JavaSabr
 */
public class ModelNodeFactory {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @Nullable
    public static <T, V extends ModelNode<T>> V createFor(@Nullable final T element) {

        final long objectId = ID_GENERATOR.incrementAndGet();

        if (element instanceof LayersRoot) {
            return unsafeCast(new LayersRootModelNode((LayersRoot) element, objectId));
        } else if (element instanceof Animation) {
            return unsafeCast(new AnimationModelNode((Animation) element, objectId));
        } else if (element instanceof BoneTrack) {
            return unsafeCast(new AnimationBoneTrackModelNode((BoneTrack) element, objectId));
        } else if (element instanceof EffectTrack) {
            return unsafeCast(new AnimationEffectTrackModelNode((EffectTrack) element, objectId));
        } else if (element instanceof AudioTrack) {
            return unsafeCast(new AnimationAudioTrackModelNode((AudioTrack) element, objectId));
        } else if (element instanceof SpatialTrack) {
            return unsafeCast(new AnimationSpatialTrackModelNode((SpatialTrack) element, objectId));
        }

        if (element instanceof KinematicRagdollControl) {
            return unsafeCast(new RagdollControlModelNode((KinematicRagdollControl) element, objectId));
        } else if (element instanceof VehicleControl) {
            return unsafeCast(new VehicleControlModelNode((VehicleControl) element, objectId));
        } else if (element instanceof SkeletonControl) {
            return unsafeCast(new SkeletonControlModelNode((SkeletonControl) element, objectId));
        } else if (element instanceof CharacterControl) {
            return unsafeCast(new CharacterControlModelNode((CharacterControl) element, objectId));
        } else if (element instanceof RigidBodyControl) {
            return unsafeCast(new RigidBodyControlModelNode((RigidBodyControl) element, objectId));
        } else if (element instanceof AnimControl) {
            return unsafeCast(new AnimationControlModelNode((AnimControl) element, objectId));
        } else if (element instanceof Control) {
            return unsafeCast(new ControlModelNode<>((Control) element, objectId));
        }

        if (element instanceof LightProbe) {
            return unsafeCast(new LightProbeModelNode((LightProbe) element, objectId));
        } else if (element instanceof AmbientLight) {
            return unsafeCast(new AmbientLightModelNode((AmbientLight) element, objectId));
        } else if (element instanceof DirectionalLight) {
            return unsafeCast(new DirectionalLightModelNode((DirectionalLight) element, objectId));
        } else if (element instanceof SpotLight) {
            return unsafeCast(new SpotLightModelNode((SpotLight) element, objectId));
        } else if (element instanceof PointLight) {
            return unsafeCast(new PointLightModelNode((PointLight) element, objectId));
        }

        if (element instanceof SceneNode) {
            return unsafeCast(new SceneNodeModelNode((SceneNode) element, objectId));
        } else if (element instanceof SceneLayer) {
            return unsafeCast(new SceneLayerModelNode((SceneLayer) element, objectId));
        } else if (element instanceof ParticleEmitterNode) {
            return unsafeCast(new ParticleEmitterNodeModelNode((ParticleEmitterNode) element, objectId));
        } else if (element instanceof ParticleInfluencers) {
            return unsafeCast(new ParticleInfluencersModelNode((ParticleInfluencers) element, objectId));
        } else if (element instanceof ParticleInfluencer) {
            return unsafeCast(new ParticleInfluencerModelNode((ParticleInfluencer) element, objectId));
        } else if (element instanceof EmitterMesh) {
            return unsafeCast(new ParticleEmitterMeshModelNode((EmitterMesh) element, objectId));
        } else if (element instanceof ParticleNode) {
            return unsafeCast(new ParticleNodeModelNode((ParticleNode) element, objectId));
        } else if (element instanceof ParticleGeometry) {
            return unsafeCast(new ParticleGeometryModelNode((ParticleGeometry) element, objectId));
        } else if (element instanceof Mesh) {
            return unsafeCast(new MeshModelNode((Mesh) element, objectId));
        } else if (element instanceof Geometry) {
            return unsafeCast(new GeometryModelNode<>((Geometry) element, objectId));
        } else if (element instanceof AudioNode) {
            return unsafeCast(new AudioModelNode((AudioNode) element, objectId));
        } else if (element instanceof Node) {
            return unsafeCast(new NodeModelNode<>((Node) element, objectId));
        }

        return null;
    }
}
