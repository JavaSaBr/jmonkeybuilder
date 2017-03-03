package com.ss.editor.ui.control.tree.node;

import static rlib.util.ClassUtils.unsafeCast;
import com.jme3.animation.*;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.light.*;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.layer.node.LayersRootModelNode;
import com.ss.editor.ui.control.layer.node.SceneLayerModelNode;
import com.ss.editor.ui.control.model.node.PositionModelNode;
import com.ss.editor.ui.control.model.node.control.ControlModelNode;
import com.ss.editor.ui.control.model.node.control.SkeletonControlModelNode;
import com.ss.editor.ui.control.model.node.control.anim.*;
import com.ss.editor.ui.control.model.node.control.motion.MotionEventModelNode;
import com.ss.editor.ui.control.model.node.control.motion.MotionPathModelNode;
import com.ss.editor.ui.control.model.node.control.physics.CharacterControlModelNode;
import com.ss.editor.ui.control.model.node.control.physics.RagdollControlModelNode;
import com.ss.editor.ui.control.model.node.control.physics.RigidBodyControlModelNode;
import com.ss.editor.ui.control.model.node.control.physics.vehicle.VehicleControlModelNode;
import com.ss.editor.ui.control.model.node.control.physics.vehicle.VehicleWheelModelNode;
import com.ss.editor.ui.control.model.node.light.*;
import com.ss.editor.ui.control.model.node.physics.shape.*;
import com.ss.editor.ui.control.model.node.spatial.AudioModelNode;
import com.ss.editor.ui.control.model.node.spatial.GeometryModelNode;
import com.ss.editor.ui.control.model.node.spatial.MeshModelNode;
import com.ss.editor.ui.control.model.node.spatial.NodeModelNode;
import com.ss.editor.ui.control.model.node.spatial.emitter.ParticleEmitterNodeModelNode;
import com.ss.editor.ui.control.model.node.spatial.emitter.ParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.node.spatial.emitter.ParticleInfluencersModelNode;
import com.ss.editor.ui.control.model.node.spatial.scene.SceneNodeModelNode;
import com.ss.editor.ui.control.model.node.spatial.terrain.TerrainGridModelNode;
import com.ss.editor.ui.control.model.node.spatial.terrain.TerrainQuadModelNode;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The factory to create the {@link ModelNode} of the element of {@link com.jme3.scene.Spatial}.
 *
 * @author JavaSabr
 */
public class ModelNodeFactory {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @Nullable
    public static <T, V extends ModelNode<T>> V createFor(@Nullable final T element) {

        final long objectId = ID_GENERATOR.incrementAndGet();

        if (element instanceof Vector3f) {
            return unsafeCast(new PositionModelNode((Vector3f) element, objectId));
        } else if (element instanceof VehicleWheel) {
            return unsafeCast(new VehicleWheelModelNode((VehicleWheel) element, objectId));
        } else if (element instanceof MotionPath) {
            return unsafeCast(new MotionPathModelNode((MotionPath) element, objectId));
        }

        if (element instanceof BoxCollisionShape) {
            return unsafeCast(new BoxCollisionShapeModelNode((BoxCollisionShape) element, objectId));
        } else if (element instanceof CapsuleCollisionShape) {
            return unsafeCast(new CapsuleCollisionShapeModelNode((CapsuleCollisionShape) element, objectId));
        } else if (element instanceof CompoundCollisionShape) {
            return unsafeCast(new ComputedCollisionShapeModelNode((CompoundCollisionShape) element, objectId));
        } else if (element instanceof ConeCollisionShape) {
            return unsafeCast(new ConeCollisionShapeModelNode((ConeCollisionShape) element, objectId));
        } else if (element instanceof CylinderCollisionShape) {
            return unsafeCast(new CylinderCollisionShapeModelNode((CylinderCollisionShape) element, objectId));
        } else if (element instanceof GImpactCollisionShape) {
            return unsafeCast(new GImpactCollisionShapeModelNode((GImpactCollisionShape) element, objectId));
        } else if (element instanceof HullCollisionShape) {
            return unsafeCast(new HullCollisionShapeModelNode((HullCollisionShape) element, objectId));
        } else if (element instanceof MeshCollisionShape) {
            return unsafeCast(new MeshCollisionShapeModelNode((MeshCollisionShape) element, objectId));
        } else if (element instanceof PlaneCollisionShape) {
            return unsafeCast(new PlaneCollisionShapeModelNode((PlaneCollisionShape) element, objectId));
        } else if (element instanceof SphereCollisionShape) {
            return unsafeCast(new SphereCollisionShapeModelNode((SphereCollisionShape) element, objectId));
        } else if (element instanceof ChildCollisionShape) {
            return unsafeCast(new ChildCollisionShapeModelNode((ChildCollisionShape) element, objectId));
        } else if (element instanceof CollisionShape) {
            return unsafeCast(new CollisionShapeModelNode<>((CollisionShape) element, objectId));
        }

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

        if (element instanceof MotionEvent) {
            return unsafeCast(new MotionEventModelNode((MotionEvent) element, objectId));
        } else if (element instanceof KinematicRagdollControl) {
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

        if (element instanceof TerrainGrid) {
            return unsafeCast(new TerrainGridModelNode((TerrainGrid) element, objectId));
        } else if (element instanceof TerrainQuad) {
            return unsafeCast(new TerrainQuadModelNode((TerrainQuad) element, objectId));
        } else if (element instanceof SceneNode) {
            return unsafeCast(new SceneNodeModelNode((SceneNode) element, objectId));
        } else if (element instanceof SceneLayer) {
            return unsafeCast(new SceneLayerModelNode((SceneLayer) element, objectId));
        } else if (element instanceof ParticleEmitterNode) {
            return unsafeCast(new ParticleEmitterNodeModelNode((ParticleEmitterNode) element, objectId));
        } else if (element instanceof ParticleInfluencers) {
            return unsafeCast(new ParticleInfluencersModelNode((ParticleInfluencers) element, objectId));
        } else if (element instanceof ParticleInfluencer) {
            return unsafeCast(new ParticleInfluencerModelNode((ParticleInfluencer) element, objectId));
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
