package com.ss.editor.ui.control.tree.node;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
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
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.jme3.effect.influencers.EmptyParticleInfluencer;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.jme3.effect.shapes.*;
import com.jme3.light.*;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.scene.control.Control;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.model.node.Toneg0dParticleInfluencers;
import com.ss.editor.ui.control.layer.LayersRoot;
import com.ss.editor.ui.control.layer.node.LayersRootModelNode;
import com.ss.editor.ui.control.layer.node.SceneLayerModelNode;
import com.ss.editor.ui.control.model.node.BufferModelNode;
import com.ss.editor.ui.control.model.node.PositionModelNode;
import com.ss.editor.ui.control.model.node.VertexBufferModelNode;
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
import com.ss.editor.ui.control.model.node.spatial.*;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.ParticleEmitterModelNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.DefaultParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.EmptyParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.ParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.influencer.RadialParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.shape.*;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.Toneg0dParticleEmitterNodeModelNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer.Toneg0dParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer.Toneg0dParticleInfluencersModelNode;
import com.ss.editor.ui.control.model.node.spatial.scene.SceneNodeModelNode;
import com.ss.editor.ui.control.model.node.spatial.terrain.TerrainGridModelNode;
import com.ss.editor.ui.control.model.node.spatial.terrain.TerrainQuadModelNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

import java.nio.Buffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The factory to create the {@link ModelNode} of an element.
 *
 * @author JavaSabr
 */
public class ModelNodeFactory {

    /**
     * The node id generator.
     */
    @NotNull
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    /**
     * Create a model node for an element.
     *
     * @param <T>     the type parameter
     * @param <V>     the type parameter
     * @param element the element
     * @return the model node.
     */
    @Nullable
    public static <T, V extends ModelNode<T>> V createFor(@Nullable final T element) {

        final long objectId = ID_GENERATOR.incrementAndGet();

        if (element instanceof Vector3f) {
            return unsafeCast(new PositionModelNode((Vector3f) element, objectId));
        } else if (element instanceof VertexBuffer) {
            return unsafeCast(new VertexBufferModelNode((VertexBuffer) element, objectId));
        } else if (element instanceof Buffer) {
            return unsafeCast(new BufferModelNode((Buffer) element, objectId));
        } else if (element instanceof VehicleWheel) {
            return unsafeCast(new VehicleWheelModelNode((VehicleWheel) element, objectId));
        } else if (element instanceof MotionPath) {
            return unsafeCast(new MotionPathModelNode((MotionPath) element, objectId));
        }

        if (element instanceof ChildCollisionShape) {
            return unsafeCast(new ChildCollisionShapeModelNode((ChildCollisionShape) element, objectId));
        } else if (element instanceof CollisionShape) {
            return getCollisionShapeModelNode((CollisionShape) element, objectId);
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

        if(element instanceof Light) {
            return getLightModelNode((Light) element, objectId);
        }

        if (element instanceof ParticleEmitter) {
            return unsafeCast(new ParticleEmitterModelNode((ParticleEmitter) element, objectId));
        } else if (element instanceof EmitterShape) {
            return getEmitterShapeModelNode((EmitterShape) element, objectId);
        } else if (element instanceof com.jme3.effect.influencers.ParticleInfluencer) {
            return getParticleInfluencerModelNode((com.jme3.effect.influencers.ParticleInfluencer) element, objectId);
        } else if (element instanceof TerrainGrid) {
            return unsafeCast(new TerrainGridModelNode((TerrainGrid) element, objectId));
        } else if (element instanceof TerrainQuad) {
            return unsafeCast(new TerrainQuadModelNode((TerrainQuad) element, objectId));
        } else if (element instanceof SceneNode) {
            return unsafeCast(new SceneNodeModelNode((SceneNode) element, objectId));
        } else if (element instanceof SceneLayer) {
            return unsafeCast(new SceneLayerModelNode((SceneLayer) element, objectId));
        } else if (element instanceof ParticleEmitterNode) {
            return unsafeCast(new Toneg0dParticleEmitterNodeModelNode((ParticleEmitterNode) element, objectId));
        } else if (element instanceof Toneg0dParticleInfluencers) {
            return unsafeCast(new Toneg0dParticleInfluencersModelNode((Toneg0dParticleInfluencers) element, objectId));
        } else if (element instanceof ParticleInfluencer) {
            return unsafeCast(new Toneg0dParticleInfluencerModelNode((ParticleInfluencer) element, objectId));
        } else if (element instanceof Mesh) {
            return unsafeCast(new MeshModelNode((Mesh) element, objectId));
        } else if (element instanceof Geometry) {
            return unsafeCast(new GeometryModelNode<>((Geometry) element, objectId));
        } else if (element instanceof AudioNode) {
            return unsafeCast(new AudioModelNode((AudioNode) element, objectId));
        } else if (element instanceof AssetLinkNode) {
            return unsafeCast(new AssetLinkNodeModelNode((AssetLinkNode) element, objectId));
        } else if (element instanceof Node) {
            return unsafeCast(new NodeModelNode<>((Node) element, objectId));
        }

        return null;
    }

    @NotNull
    private static <T, V extends ModelNode<T>> V getParticleInfluencerModelNode(
            @NotNull final com.jme3.effect.influencers.ParticleInfluencer element, final long objectId) {

        if (element instanceof EmptyParticleInfluencer) {
            return unsafeCast(new EmptyParticleInfluencerModelNode((EmptyParticleInfluencer) element, objectId));
        } else if (element instanceof RadialParticleInfluencer) {
            return unsafeCast(new RadialParticleInfluencerModelNode((RadialParticleInfluencer) element, objectId));
        } else if (element instanceof DefaultParticleInfluencer) {
            return unsafeCast(new DefaultParticleInfluencerModelNode((DefaultParticleInfluencer) element, objectId));
        }

        return unsafeCast(new ParticleInfluencerModelNode(element, objectId));
    }

    @NotNull
    private static <T, V extends ModelNode<T>> V getCollisionShapeModelNode(@NotNull final CollisionShape element,
                                                                            final long objectId) {

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
        }

        return unsafeCast(new CollisionShapeModelNode<>(element, objectId));
    }

    @NotNull
    private static <T, V extends ModelNode<T>> V getLightModelNode(@NotNull final Light element, final long objectId) {

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

        throw new IllegalArgumentException("Unsupported light " + element);
    }

    @NotNull
    private static <T, V extends ModelNode<T>> V getEmitterShapeModelNode(@NotNull final EmitterShape element,
                                                                          final long objectId) {
        if (element instanceof EmitterBoxShape) {
            return unsafeCast(new EmitterBoxShapeModelNode((EmitterBoxShape) element, objectId));
        } else if (element instanceof EmitterSphereShape) {
            return unsafeCast(new EmitterSphereShapeModelNode((EmitterSphereShape) element, objectId));
        } else if (element instanceof EmitterPointShape) {
            return unsafeCast(new EmitterPointShapeModelNode((EmitterPointShape) element, objectId));
        } else if (element instanceof EmitterMeshConvexHullShape) {
            return unsafeCast(new EmitterMeshConvexHullShapeModelNode((EmitterMeshConvexHullShape) element, objectId));
        } else if (element instanceof EmitterMeshFaceShape) {
            return unsafeCast(new EmitterMeshFaceShapeModelNode((EmitterMeshFaceShape) element, objectId));
        } else if (element instanceof EmitterMeshVertexShape) {
            return unsafeCast(new EmitterMeshVertexShapeModelNode((EmitterMeshVertexShape) element, objectId));
        }

        return unsafeCast(new EmitterShapeModelNode(element, objectId));
    }
}
