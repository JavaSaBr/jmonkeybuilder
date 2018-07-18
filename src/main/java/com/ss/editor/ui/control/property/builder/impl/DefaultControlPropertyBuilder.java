package com.ss.editor.ui.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import com.jme3.animation.Animation;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.cinematic.events.AbstractCinematicEvent;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for default controls.
 *
 * @author JavaSaBr
 */
public class DefaultControlPropertyBuilder extends EditableModelObjectPropertyBuilder {

    public static final int PRIORITY = 0;

    private static final PropertyBuilder INSTANCE = new DefaultControlPropertyBuilder();

    @FxThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private DefaultControlPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @FxThread
    protected @NotNull List<EditableProperty<?, ?>> getProperties(@NotNull AbstractControl control) {

        var result = new ArrayList<EditableProperty<?, ?>>();
        result.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_ENABLED, control,
                AbstractControl::isEnabled, AbstractControl::setEnabled));

        return result;
    }

    @FxThread
    protected @NotNull List<EditableProperty<?, ?>> getProperties(@NotNull PhysicsControl control) {

        var result = new ArrayList<EditableProperty<?, ?>>();
        result.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_ENABLED, control,
                PhysicsControl::isEnabled, PhysicsControl::setEnabled));

        return result;
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        var properties = new ArrayList<EditableProperty<?, ?>>();

        if (object instanceof VehicleWheel) {

            var control = (VehicleWheel) object;

            properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_FRONT, control,
                    VehicleWheel::isFrontWheel, VehicleWheel::setFrontWheel));
            properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL, control,
                    VehicleWheel::isApplyLocal, VehicleWheel::setApplyLocal));
            properties.add(new SimpleProperty<>(STRING, Messages.MODEL_PROPERTY_OBJECT_ID, control,
                    wheel -> String.valueOf(wheel.getWheelId())));

            properties.add(new SimpleProperty<>(SPATIAL_FROM_SCENE, Messages.MODEL_PROPERTY_WHEEL_SPATIAL, control,
                    VehicleWheel::getWheelSpatial, VehicleWheel::setWheelSpatial));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_LOCATION, control,
                    VehicleWheel::getLocation));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_DIRECTION, control,
                    VehicleWheel::getDirection));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_AXLE, control,
                    VehicleWheel::getAxle));

            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_DAMPING_COMPRESSION, control,
                    VehicleWheel::getWheelsDampingCompression, VehicleWheel::setWheelsDampingCompression));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_FRICTION_SLIP, control,
                    VehicleWheel::getFrictionSlip, VehicleWheel::setFrictionSlip));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_RADIUS, control,
                    VehicleWheel::getRadius, VehicleWheel::setRadius));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_MAX_SUSPENSION_FORCE, control,
                    VehicleWheel::getMaxSuspensionForce, VehicleWheel::setMaxSuspensionForce));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_MAX_SUSPENSION_TRAVEL_CM, control,
                    VehicleWheel::getMaxSuspensionTravelCm, VehicleWheel::setMaxSuspensionTravelCm));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_MAX_SUSPENSION_TRAVEL_CM, control,
                    VehicleWheel::getMaxSuspensionTravelCm, VehicleWheel::setMaxSuspensionTravelCm));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_DAMPING_RELAXATION, control,
                    VehicleWheel::getWheelsDampingRelaxation, VehicleWheel::setWheelsDampingRelaxation));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_SUSPENSION_STIFFNESS, control,
                    VehicleWheel::getSuspensionStiffness, VehicleWheel::setSuspensionStiffness));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_REST_LENGTH, control,
                    VehicleWheel::getRestLength, VehicleWheel::setRestLength));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_ROLL_INFLUENCE, control,
                    VehicleWheel::getRollInfluence, VehicleWheel::setRollInfluence));

        } else if (object instanceof AbstractCinematicEvent) {

            var control = (AbstractCinematicEvent) object;

            properties.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_LOOP_MODE, control,
                    AbstractCinematicEvent::getLoopMode, AbstractCinematicEvent::setLoopMode));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_INITIAL_DURATION, control,
                    AbstractCinematicEvent::getInitialDuration, AbstractCinematicEvent::setInitialDuration));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_SPEED, control,
                    AbstractCinematicEvent::getSpeed, AbstractCinematicEvent::setSpeed));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_TIME, control,
                    AbstractCinematicEvent::getTime, AbstractCinematicEvent::setTime));

        } else if (object instanceof Animation) {

            var animation = (Animation) object;

            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_LENGTH, animation,
                    Animation::getLength));
        }

        if (object instanceof MotionEvent) {

            var control = (MotionEvent) object;

            properties.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_DIRECTION_TYPE, control,
                    MotionEvent::getDirectionType, MotionEvent::setDirectionType));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_DIRECTION, control,
                    MotionEvent::getDirection, MotionEvent::setDirection));
            properties.add(new SimpleProperty<>(QUATERNION, Messages.MODEL_PROPERTY_ROTATION, control,
                    MotionEvent::getRotation, MotionEvent::setRotation));
        }

        if (!(object instanceof Control)) {
            return properties;
        }

        if (object instanceof AbstractControl) {
            properties.addAll(getProperties((AbstractControl) object));
        }

        if (object instanceof PhysicsControl) {
            properties.addAll(getProperties((PhysicsControl) object));
        }

        if (object instanceof LightControl) {

            var control = (LightControl) object;

            properties.add(new SimpleProperty<>(ENUM, Messages.MODEL_PROPERTY_DIRECTION_TYPE, control,
                    LightControl::getControlDir, LightControl::setControlDir));
            properties.add(new SimpleProperty<>(LIGHT_FROM_SCENE, Messages.MODEL_PROPERTY_LIGHT, control,
                    LightControl::getLight, LightControl::setLight));

        } else if (object instanceof BetterCharacterControl) {

            var control = (BetterCharacterControl) object;

            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_GRAVITY, control,
                    BetterCharacterControl::getGravity, BetterCharacterControl::setGravity));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_VELOCITY, control,
                    BetterCharacterControl::getVelocity));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_GRAVITY, control,
                    BetterCharacterControl::getViewDirection, BetterCharacterControl::setViewDirection));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_WALK_DIRECTION, control,
                    BetterCharacterControl::getWalkDirection, BetterCharacterControl::setWalkDirection));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_JUMP_FORCE, control,
                    BetterCharacterControl::getJumpForce, BetterCharacterControl::setJumpForce));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_WALK_DIRECTION, control,
                    BetterCharacterControl::getDuckedFactor, BetterCharacterControl::setDuckedFactor));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_PHYSICS_DAMPING, control,
                    BetterCharacterControl::getPhysicsDamping, BetterCharacterControl::setPhysicsDamping));

        } else if (object instanceof RigidBodyControl) {

            var control = (RigidBodyControl) object;

            properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_KINEMATIC_SPATIAL, control,
                    RigidBodyControl::isKinematicSpatial, RigidBodyControl::setKinematicSpatial));

        } else if (object instanceof SkeletonControl) {

            var control = (SkeletonControl) object;

            properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_HARDWARE_SKINNING_PREFERRED, control,
                    SkeletonControl::isHardwareSkinningPreferred, SkeletonControl::setHardwareSkinningPreferred));

        } else if (object instanceof VehicleControl) {

           var control = (VehicleControl) object;

            properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL, control,
                    VehicleControl::isApplyPhysicsLocal, VehicleControl::setApplyPhysicsLocal));

        }

        if (object instanceof PhysicsRigidBody) {

            var control = (PhysicsRigidBody) object;

            properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_KINEMATIC, control,
                    PhysicsRigidBody::isKinematic, PhysicsRigidBody::setKinematic));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_GRAVITY, control,
                    PhysicsRigidBody::getGravity, PhysicsRigidBody::setGravity));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_LINEAR_FACTOR, control,
                    PhysicsRigidBody::getLinearFactor, PhysicsRigidBody::setLinearFactor));
            properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_ANGULAR_VELOCITY, control,
                    PhysicsRigidBody::getAngularVelocity, PhysicsRigidBody::setAngularVelocity));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_ANGULAR_DAMPING, control,
                    PhysicsRigidBody::getAngularDamping, PhysicsRigidBody::setAngularDamping));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_ANGULAR_FACTOR, control,
                    PhysicsRigidBody::getAngularFactor, PhysicsRigidBody::setAngularFactor));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_ANGULAR_SLEEPING_THRESHOLD, control,
                    PhysicsRigidBody::getAngularSleepingThreshold, PhysicsRigidBody::setAngularSleepingThreshold));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_FRICTION, control,
                    PhysicsRigidBody::getFriction, PhysicsRigidBody::setFriction));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_MASS, control,
                    PhysicsRigidBody::getMass, PhysicsRigidBody::setMass));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_LINEAR_DAMPING, control,
                    PhysicsRigidBody::getLinearDamping, PhysicsRigidBody::setLinearDamping));
            properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_RESTITUTION, control,
                    PhysicsRigidBody::getRestitution, PhysicsRigidBody::setRestitution));
        }

        return properties;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
