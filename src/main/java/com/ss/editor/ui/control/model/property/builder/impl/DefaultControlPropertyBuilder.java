package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.cinematic.events.AbstractCinematicEvent;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.*;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for default controls.
 *
 * @author JavaSaBr
 */
public class DefaultControlPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final PropertyBuilder INSTANCE = new DefaultControlPropertyBuilder();

    @NotNull
    private static final MotionEvent.Direction[] DIRECTIONS = MotionEvent.Direction.values();

    @NotNull
    private static final LoopMode[] LOOP_MODES = LoopMode.values();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private DefaultControlPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final ModelChangeConsumer changeConsumer) {

        if (object instanceof AbstractCinematicEvent) {
            build((AbstractCinematicEvent) object, container, changeConsumer);
        }  else if (object instanceof VehicleWheel) {
            build((VehicleWheel) object, container, changeConsumer);
        } else if (object instanceof Animation) {
            build((Animation) object, container, changeConsumer);
        }

        if (!(object instanceof Control)) return;

        if (object instanceof AbstractControl) {
            build((AbstractControl) object, container, changeConsumer);
        }

        if (object instanceof SkeletonControl) {
            build((SkeletonControl) object, container, changeConsumer);
        } else if (object instanceof CharacterControl) {
            build((CharacterControl) object, container, changeConsumer);
        } else if (object instanceof RigidBodyControl) {
            build((RigidBodyControl) object, container, changeConsumer);
        } else if (object instanceof VehicleControl) {
            build((VehicleControl) object, container, changeConsumer);
        } else if (object instanceof MotionEvent) {
            build((MotionEvent) object, container, changeConsumer);
        }

        if (object instanceof PhysicsRigidBody) {
            build((PhysicsRigidBody) object, container, changeConsumer);
        }
    }

    private void build(@NotNull final AbstractCinematicEvent control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final LoopMode loopMode = control.getLoopMode();

        final float initialDuration = control.getInitialDuration();
        final float speed = control.getSpeed();
        final float time = control.getTime();

        final EnumModelPropertyControl<AbstractCinematicEvent, LoopMode> loopModeControl =
                new EnumModelPropertyControl<>(loopMode, Messages.MODEL_PROPERTY_LOOP_MODE, changeConsumer, LOOP_MODES);

        loopModeControl.setApplyHandler(AbstractCinematicEvent::setLoopMode);
        loopModeControl.setSyncHandler(AbstractCinematicEvent::getLoopMode);
        loopModeControl.setEditObject(control);

        final FloatModelPropertyControl<AbstractCinematicEvent> initialDurationControl =
                new FloatModelPropertyControl<>(initialDuration, Messages.MODEL_PROPERTY_INITIAL_DURATION, changeConsumer);

        initialDurationControl.setApplyHandler(AbstractCinematicEvent::setInitialDuration);
        initialDurationControl.setSyncHandler(AbstractCinematicEvent::getInitialDuration);
        initialDurationControl.setEditObject(control);

        final FloatModelPropertyControl<AbstractCinematicEvent> speedControl =
                new FloatModelPropertyControl<>(speed, Messages.MODEL_PROPERTY_SPEED, changeConsumer);

        speedControl.setApplyHandler(AbstractCinematicEvent::setSpeed);
        speedControl.setSyncHandler(AbstractCinematicEvent::getSpeed);
        speedControl.setEditObject(control);

        final FloatModelPropertyControl<AbstractCinematicEvent> timeControl =
                new FloatModelPropertyControl<>(time, Messages.MODEL_PROPERTY_TIME, changeConsumer);

        timeControl.setApplyHandler(AbstractCinematicEvent::setSpeed);
        timeControl.setSyncHandler(AbstractCinematicEvent::getSpeed);
        timeControl.setEditObject(control);

        FXUtils.addToPane(loopModeControl, container);
        FXUtils.addToPane(initialDurationControl, container);
        FXUtils.addToPane(speedControl, container);
        FXUtils.addToPane(timeControl, container);
    }

    private void build(@NotNull final AbstractControl control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final boolean enabled = control.isEnabled();

        final BooleanModelPropertyControl<AbstractControl> enabledControl =
                new BooleanModelPropertyControl<>(enabled, Messages.MODEL_PROPERTY_IS_ENABLED, changeConsumer);

        enabledControl.setApplyHandler(AbstractControl::setEnabled);
        enabledControl.setSyncHandler(AbstractControl::isEnabled);
        enabledControl.setEditObject(control);

        FXUtils.addToPane(enabledControl, container);
    }

    private void build(@NotNull final MotionEvent control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f direction = control.getDirection();
        final Quaternion rotation = control.getRotation();

        final MotionEvent.Direction directionType = control.getDirectionType();

        final EnumModelPropertyControl<MotionEvent, MotionEvent.Direction> directionTypeControl =
                new EnumModelPropertyControl<>(directionType, Messages.MODEL_PROPERTY_DIRECTION_TYPE, changeConsumer, DIRECTIONS);

        directionTypeControl.setApplyHandler(MotionEvent::setDirectionType);
        directionTypeControl.setSyncHandler(MotionEvent::getDirectionType);
        directionTypeControl.setEditObject(control);

        final Vector3fModelPropertyControl<MotionEvent> directionControl =
                new Vector3fModelPropertyControl<>(direction, Messages.MODEL_PROPERTY_DIRECTION, changeConsumer);

        directionControl.setApplyHandler(MotionEvent::setDirection);
        directionControl.setSyncHandler(MotionEvent::getDirection);
        directionControl.setEditObject(control);

        final QuaternionModelPropertyControl<MotionEvent> rotationControl =
                new QuaternionModelPropertyControl<>(rotation, Messages.MODEL_PROPERTY_ROTATION, changeConsumer);

        rotationControl.setApplyHandler(MotionEvent::setRotation);
        rotationControl.setSyncHandler(MotionEvent::getRotation);
        rotationControl.setEditObject(control);

        FXUtils.addToPane(directionTypeControl, container);

        buildSplitLine(container);

        FXUtils.addToPane(directionControl, container);
        FXUtils.addToPane(rotationControl, container);
    }

    private void build(@NotNull final CharacterControl control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f viewDirection = control.getViewDirection();
        final Vector3f walkDirection = control.getWalkDirection();

        final float fallSpeed = control.getFallSpeed();
        final float gravity = control.getGravity();
        final float jumpSpeed = control.getJumpSpeed();
        final float maxSlope = control.getMaxSlope();

        final boolean applyPhysicsLocal = control.isApplyPhysicsLocal();
        final boolean useViewDirection = control.isUseViewDirection();
        final boolean enabled = control.isEnabled();

        final BooleanModelPropertyControl<CharacterControl> enabledControl =
                new BooleanModelPropertyControl<>(enabled, Messages.MODEL_PROPERTY_IS_ENABLED, changeConsumer);

        enabledControl.setApplyHandler(CharacterControl::setEnabled);
        enabledControl.setSyncHandler(CharacterControl::isEnabled);
        enabledControl.setEditObject(control);

        final Vector3fModelPropertyControl<CharacterControl> viewDirectionControl =
                new Vector3fModelPropertyControl<>(viewDirection, Messages.MODEL_PROPERTY_VIEW_DIRECTION, changeConsumer);

        viewDirectionControl.setApplyHandler(CharacterControl::setViewDirection);
        viewDirectionControl.setSyncHandler(CharacterControl::getViewDirection);
        viewDirectionControl.setEditObject(control);

        final Vector3fModelPropertyControl<CharacterControl> walkDirectionControl =
                new Vector3fModelPropertyControl<>(walkDirection, Messages.MODEL_PROPERTY_WALK_DIRECTION, changeConsumer);

        walkDirectionControl.setApplyHandler(CharacterControl::setWalkDirection);
        walkDirectionControl.setSyncHandler(CharacterControl::getWalkDirection);
        walkDirectionControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> fallSpeedControl =
                new FloatModelPropertyControl<>(fallSpeed, Messages.MODEL_PROPERTY_FALL_SPEED, changeConsumer);

        fallSpeedControl.setApplyHandler(CharacterControl::setFallSpeed);
        fallSpeedControl.setSyncHandler(CharacterControl::getFallSpeed);
        fallSpeedControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> gravityControl =
                new FloatModelPropertyControl<>(gravity, Messages.MODEL_PROPERTY_GRAVITY, changeConsumer);

        gravityControl.setApplyHandler(CharacterControl::setGravity);
        gravityControl.setSyncHandler(CharacterControl::getGravity);
        gravityControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> jumpSpeedControl =
                new FloatModelPropertyControl<>(jumpSpeed, Messages.MODEL_PROPERTY_JUMP_SPEED, changeConsumer);

        jumpSpeedControl.setApplyHandler(CharacterControl::setJumpSpeed);
        jumpSpeedControl.setSyncHandler(CharacterControl::getJumpSpeed);
        jumpSpeedControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> maxSlopeControl =
                new FloatModelPropertyControl<>(maxSlope, Messages.MODEL_PROPERTY_MAX_SLOPE, changeConsumer);

        maxSlopeControl.setApplyHandler(CharacterControl::setMaxSlope);
        maxSlopeControl.setSyncHandler(CharacterControl::getMaxSlope);
        maxSlopeControl.setEditObject(control);

        final BooleanModelPropertyControl<CharacterControl> applyPhysicsLocalControl =
                new BooleanModelPropertyControl<>(applyPhysicsLocal, Messages.MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL, changeConsumer);

        applyPhysicsLocalControl.setApplyHandler(CharacterControl::setApplyPhysicsLocal);
        applyPhysicsLocalControl.setSyncHandler(CharacterControl::isApplyPhysicsLocal);
        applyPhysicsLocalControl.setEditObject(control);

        final BooleanModelPropertyControl<CharacterControl> useViewDirectionControl =
                new BooleanModelPropertyControl<>(useViewDirection, Messages.MODEL_PROPERTY_IS_USE_VIEW_DIRECTION, changeConsumer);

        useViewDirectionControl.setApplyHandler(CharacterControl::setUseViewDirection);
        useViewDirectionControl.setSyncHandler(CharacterControl::isUseViewDirection);
        useViewDirectionControl.setEditObject(control);

        FXUtils.addToPane(enabledControl, container);
        FXUtils.addToPane(applyPhysicsLocalControl, container);
        FXUtils.addToPane(useViewDirectionControl, container);
        FXUtils.addToPane(fallSpeedControl, container);
        FXUtils.addToPane(gravityControl, container);
        FXUtils.addToPane(jumpSpeedControl, container);
        FXUtils.addToPane(maxSlopeControl, container);

        buildSplitLine(container);

        FXUtils.addToPane(viewDirectionControl, container);
        FXUtils.addToPane(walkDirectionControl, container);
    }

    private void build(@NotNull final SkeletonControl control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final boolean hardwareSkinningPreferred = control.isHardwareSkinningPreferred();

        final BooleanModelPropertyControl<SkeletonControl> hardwareSkinningPreferredControl =
                new BooleanModelPropertyControl<>(hardwareSkinningPreferred, Messages.MODEL_PROPERTY_IS_HARDWARE_SKINNING_PREFERRED, changeConsumer);

        hardwareSkinningPreferredControl.setApplyHandler(SkeletonControl::setHardwareSkinningPreferred);
        hardwareSkinningPreferredControl.setSyncHandler(SkeletonControl::isHardwareSkinningPreferred);
        hardwareSkinningPreferredControl.setEditObject(control);

        FXUtils.addToPane(hardwareSkinningPreferredControl, container);
    }

    private void build(@NotNull final Animation animation, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final float length = animation.getLength();

        final DefaultModelSinglePropertyControl<Animation, Float> lengthControl =
                new DefaultModelSinglePropertyControl<>(length, Messages.MODEL_PROPERTY_LENGTH, changeConsumer);

        lengthControl.setSyncHandler(Animation::getLength);
        lengthControl.setToStringFunction(value -> Float.toString(value));
        lengthControl.setEditObject(animation);

        FXUtils.addToPane(lengthControl, container);
    }

    private void build(@NotNull final RigidBodyControl control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {


        final boolean kinematicSpatial = control.isKinematicSpatial();
        final boolean enabled = control.isEnabled();

        final BooleanModelPropertyControl<RigidBodyControl> enabledControl =
                new BooleanModelPropertyControl<>(enabled, Messages.MODEL_PROPERTY_IS_ENABLED, changeConsumer);

        enabledControl.setApplyHandler(RigidBodyControl::setEnabled);
        enabledControl.setSyncHandler(RigidBodyControl::isEnabled);
        enabledControl.setEditObject(control);

        final BooleanModelPropertyControl<RigidBodyControl> kinematicSpatialControl =
                new BooleanModelPropertyControl<>(kinematicSpatial, Messages.MODEL_PROPERTY_IS_KINEMATIC_SPATIAL, changeConsumer);

        kinematicSpatialControl.setApplyHandler(RigidBodyControl::setKinematicSpatial);
        kinematicSpatialControl.setSyncHandler(RigidBodyControl::isKinematicSpatial);
        kinematicSpatialControl.setEditObject(control);

        FXUtils.addToPane(enabledControl, container);
        FXUtils.addToPane(kinematicSpatialControl, container);
    }

    private void build(@NotNull final VehicleControl control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final boolean enabled = control.isEnabled();
        final boolean applyPhysicsLocal = control.isApplyPhysicsLocal();

        final BooleanModelPropertyControl<VehicleControl> enabledControl =
                new BooleanModelPropertyControl<>(enabled, Messages.MODEL_PROPERTY_IS_ENABLED, changeConsumer);

        enabledControl.setApplyHandler(VehicleControl::setEnabled);
        enabledControl.setSyncHandler(VehicleControl::isEnabled);
        enabledControl.setEditObject(control);

        final BooleanModelPropertyControl<VehicleControl> applyPhysicsLocalControl =
                new BooleanModelPropertyControl<>(applyPhysicsLocal, Messages.MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL, changeConsumer);

        applyPhysicsLocalControl.setApplyHandler(VehicleControl::setApplyPhysicsLocal);
        applyPhysicsLocalControl.setSyncHandler(VehicleControl::isApplyPhysicsLocal);
        applyPhysicsLocalControl.setEditObject(control);

        FXUtils.addToPane(enabledControl, container);
        FXUtils.addToPane(applyPhysicsLocalControl, container);
    }

    private void build(@NotNull final VehicleWheel control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f axle = control.getAxle();
        final Vector3f direction = control.getDirection();
        final Vector3f location = control.getLocation();

        final Spatial wheelSpatial = control.getWheelSpatial();

        final long wheelId = control.getWheelId();

        final float frictionSlip = control.getFrictionSlip();
        final float maxSuspensionForce = control.getMaxSuspensionForce();
        final float maxSuspensionTravelCm = control.getMaxSuspensionTravelCm();
        final float radius = control.getRadius();
        final float wheelsDampingCompression = control.getWheelsDampingCompression();
        final float restLength = control.getRestLength();
        final float rollInfluence = control.getRollInfluence();
        final float suspensionStiffness = control.getSuspensionStiffness();
        final float wheelsDampingRelaxation = control.getWheelsDampingRelaxation();

        final boolean frontWheel = control.isFrontWheel();
        final boolean applyLocal = control.isApplyLocal();

        final BooleanModelPropertyControl<VehicleWheel> frontWheelControl =
                new BooleanModelPropertyControl<>(frontWheel, Messages.MODEL_PROPERTY_IS_FRONT, changeConsumer);

        frontWheelControl.setApplyHandler(VehicleWheel::setFrontWheel);
        frontWheelControl.setSyncHandler(VehicleWheel::isFrontWheel);
        frontWheelControl.setEditObject(control);

        final BooleanModelPropertyControl<VehicleWheel> applyLocalControl =
                new BooleanModelPropertyControl<>(applyLocal, Messages.MODEL_PROPERTY_IS_APPLY_PHYSICS_LOCAL, changeConsumer);

        applyLocalControl.setApplyHandler(VehicleWheel::setApplyLocal);
        applyLocalControl.setSyncHandler(VehicleWheel::isApplyLocal);
        applyLocalControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> dampingCompressionControl =
                new FloatModelPropertyControl<>(wheelsDampingCompression, Messages.MODEL_PROPERTY_DAMPING_COMPRESSION, changeConsumer);

        dampingCompressionControl.setApplyHandler(VehicleWheel::setWheelsDampingCompression);
        dampingCompressionControl.setSyncHandler(VehicleWheel::getWheelsDampingCompression);
        dampingCompressionControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> frictionSlipControl =
                new FloatModelPropertyControl<>(frictionSlip, Messages.MODEL_PROPERTY_FRICTION_SLIP, changeConsumer);

        frictionSlipControl.setApplyHandler(VehicleWheel::setFrictionSlip);
        frictionSlipControl.setSyncHandler(VehicleWheel::getFrictionSlip);
        frictionSlipControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> radiusControl =
                new FloatModelPropertyControl<>(radius, Messages.MODEL_PROPERTY_RADIUS, changeConsumer);

        radiusControl.setApplyHandler(VehicleWheel::setRadius);
        radiusControl.setSyncHandler(VehicleWheel::getRadius);
        radiusControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> maxSuspensionForceControl =
                new FloatModelPropertyControl<>(maxSuspensionForce, Messages.MODEL_PROPERTY_MAX_SUSPENSION_FORCE, changeConsumer);

        maxSuspensionForceControl.setApplyHandler(VehicleWheel::setMaxSuspensionForce);
        maxSuspensionForceControl.setSyncHandler(VehicleWheel::getMaxSuspensionForce);
        maxSuspensionForceControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> maxSuspensionTravelCmControl =
                new FloatModelPropertyControl<>(maxSuspensionTravelCm, Messages.MODEL_PROPERTY_MAX_SUSPENSION_TRAVEL_CM, changeConsumer);

        maxSuspensionTravelCmControl.setApplyHandler(VehicleWheel::setMaxSuspensionTravelCm);
        maxSuspensionTravelCmControl.setSyncHandler(VehicleWheel::getMaxSuspensionTravelCm);
        maxSuspensionTravelCmControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> wheelsDampingRelaxationControl =
                new FloatModelPropertyControl<>(wheelsDampingRelaxation, Messages.MODEL_PROPERTY_DAMPING_RELAXATION, changeConsumer);

        wheelsDampingRelaxationControl.setApplyHandler(VehicleWheel::setWheelsDampingRelaxation);
        wheelsDampingRelaxationControl.setSyncHandler(VehicleWheel::getWheelsDampingRelaxation);
        wheelsDampingRelaxationControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> suspensionStiffnessControl =
                new FloatModelPropertyControl<>(suspensionStiffness, Messages.MODEL_PROPERTY_SUSPENSION_STIFFNESS, changeConsumer);

        suspensionStiffnessControl.setApplyHandler(VehicleWheel::setSuspensionStiffness);
        suspensionStiffnessControl.setSyncHandler(VehicleWheel::getSuspensionStiffness);
        suspensionStiffnessControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> restLengthControl =
                new FloatModelPropertyControl<>(restLength, Messages.MODEL_PROPERTY_REST_LENGTH, changeConsumer);

        restLengthControl.setApplyHandler(VehicleWheel::setRestLength);
        restLengthControl.setSyncHandler(VehicleWheel::getRestLength);
        restLengthControl.setEditObject(control);

        final FloatModelPropertyControl<VehicleWheel> rollInfluenceControl =
                new FloatModelPropertyControl<>(rollInfluence, Messages.MODEL_PROPERTY_ROLL_INFLUENCE, changeConsumer);

        rollInfluenceControl.setApplyHandler(VehicleWheel::setRollInfluence);
        rollInfluenceControl.setSyncHandler(VehicleWheel::getRollInfluence);
        rollInfluenceControl.setEditObject(control);

        final WheelElementModelPropertyControl wheelSpatialControl =
                new WheelElementModelPropertyControl(wheelSpatial, Messages.MODEL_PROPERTY_WHEEL_SPATIAL, changeConsumer);

        wheelSpatialControl.setApplyHandler(VehicleWheel::setWheelSpatial);
        wheelSpatialControl.setSyncHandler(VehicleWheel::getWheelSpatial);
        wheelSpatialControl.setEditObject(control);

        final DefaultModelSinglePropertyControl<VehicleWheel, Long> wheelIdControl =
                new DefaultModelSinglePropertyControl<>(wheelId, Messages.MODEL_PROPERTY_OBJECT_ID, changeConsumer);

        wheelIdControl.setSyncHandler(VehicleWheel::getWheelId);
        wheelIdControl.setToStringFunction(value -> Long.toString(value));
        wheelIdControl.setEditObject(control);

        final DefaultModelSinglePropertyControl<VehicleWheel, Vector3f> locationControl =
                new DefaultModelSinglePropertyControl<>(location, Messages.MODEL_PROPERTY_LOCATION, changeConsumer);

        locationControl.setSyncHandler(VehicleWheel::getLocation);
        locationControl.setToStringFunction(Vector3f::toString);
        locationControl.setEditObject(control);

        final DefaultModelSinglePropertyControl<VehicleWheel, Vector3f> directionControl =
                new DefaultModelSinglePropertyControl<>(direction, Messages.MODEL_PROPERTY_DIRECTION, changeConsumer);

        directionControl.setSyncHandler(VehicleWheel::getDirection);
        directionControl.setToStringFunction(Vector3f::toString);
        directionControl.setEditObject(control);

        final DefaultModelSinglePropertyControl<VehicleWheel, Vector3f> axleControl =
                new DefaultModelSinglePropertyControl<>(axle, Messages.MODEL_PROPERTY_AXLE, changeConsumer);

        axleControl.setSyncHandler(VehicleWheel::getAxle);
        axleControl.setToStringFunction(Vector3f::toString);
        axleControl.setEditObject(control);

        FXUtils.addToPane(frontWheelControl, container);
        FXUtils.addToPane(applyLocalControl, container);
        FXUtils.addToPane(dampingCompressionControl, container);
        FXUtils.addToPane(frictionSlipControl, container);
        FXUtils.addToPane(radiusControl, container);
        FXUtils.addToPane(maxSuspensionForceControl, container);
        FXUtils.addToPane(maxSuspensionTravelCmControl, container);
        FXUtils.addToPane(wheelsDampingRelaxationControl, container);
        FXUtils.addToPane(suspensionStiffnessControl, container);
        FXUtils.addToPane(restLengthControl, container);
        FXUtils.addToPane(rollInfluenceControl, container);
        FXUtils.addToPane(wheelSpatialControl, container);
        FXUtils.addToPane(wheelIdControl, container);
        FXUtils.addToPane(locationControl, container);
        FXUtils.addToPane(directionControl, container);
        FXUtils.addToPane(axleControl, container);
    }

    private void build(@NotNull final PhysicsRigidBody control, @NotNull final VBox container,
                       @NotNull final ModelChangeConsumer changeConsumer) {

        final Vector3f angularVelocity = control.getAngularVelocity();
        final Vector3f gravity = control.getGravity();
        final Vector3f linearFactor = control.getLinearFactor();

        final float angularDamping = control.getAngularDamping();
        final float angularFactor = control.getAngularFactor();
        final float angularSleepingThreshold = control.getAngularSleepingThreshold();
        final float friction = control.getFriction();
        final float linearDamping = control.getLinearDamping();
        final float mass = control.getMass();
        final float restitution = control.getRestitution();

        final boolean kinematic = control.isKinematic();

        final BooleanModelPropertyControl<PhysicsRigidBody> kinematicControl =
                new BooleanModelPropertyControl<>(kinematic, Messages.MODEL_PROPERTY_IS_KINEMATIC, changeConsumer);

        kinematicControl.setApplyHandler(PhysicsRigidBody::setKinematic);
        kinematicControl.setSyncHandler(PhysicsRigidBody::isKinematic);
        kinematicControl.setEditObject(control);

        final Vector3fModelPropertyControl<PhysicsRigidBody> angularVelocityControl =
                new Vector3fModelPropertyControl<>(angularVelocity, Messages.MODEL_PROPERTY_ANGULAR_VELOCITY, changeConsumer);

        angularVelocityControl.setApplyHandler(PhysicsRigidBody::setAngularVelocity);
        angularVelocityControl.setSyncHandler(PhysicsRigidBody::getAngularVelocity);
        angularVelocityControl.setEditObject(control);

        final Vector3fModelPropertyControl<PhysicsRigidBody> gravityControl =
                new Vector3fModelPropertyControl<>(gravity, Messages.MODEL_PROPERTY_GRAVITY, changeConsumer);

        gravityControl.setApplyHandler(PhysicsRigidBody::setGravity);
        gravityControl.setSyncHandler(PhysicsRigidBody::getGravity);
        gravityControl.setEditObject(control);

        final Vector3fModelPropertyControl<PhysicsRigidBody> linearFactorControl =
                new Vector3fModelPropertyControl<>(linearFactor, Messages.MODEL_PROPERTY_LINEAR_FACTOR, changeConsumer);

        linearFactorControl.setApplyHandler(PhysicsRigidBody::setLinearFactor);
        linearFactorControl.setSyncHandler(PhysicsRigidBody::getLinearFactor);
        linearFactorControl.setEditObject(control);

        final FloatModelPropertyControl<PhysicsRigidBody> angularDampingControl =
                new FloatModelPropertyControl<>(angularDamping, Messages.MODEL_PROPERTY_ANGULAR_DAMPING, changeConsumer);

        angularDampingControl.setApplyHandler(PhysicsRigidBody::setAngularDamping);
        angularDampingControl.setSyncHandler(PhysicsRigidBody::getAngularDamping);
        angularDampingControl.setMinMax(0F, 1F);
        angularDampingControl.setScrollPower(1F);
        angularDampingControl.setEditObject(control);

        final FloatModelPropertyControl<PhysicsRigidBody> angularFactorControl =
                new FloatModelPropertyControl<>(angularFactor, Messages.MODEL_PROPERTY_ANGULAR_FACTOR, changeConsumer);

        angularFactorControl.setApplyHandler(PhysicsRigidBody::setAngularFactor);
        angularFactorControl.setSyncHandler(PhysicsRigidBody::getAngularFactor);
        angularFactorControl.setEditObject(control);

        final FloatModelPropertyControl<PhysicsRigidBody> angularSleepingThresholdControl =
                new FloatModelPropertyControl<>(angularSleepingThreshold, Messages.MODEL_PROPERTY_ANGULAR_SLEEPING_THRESHOLD, changeConsumer);

        angularSleepingThresholdControl.setApplyHandler(PhysicsRigidBody::setAngularSleepingThreshold);
        angularSleepingThresholdControl.setSyncHandler(PhysicsRigidBody::getAngularSleepingThreshold);
        angularSleepingThresholdControl.setEditObject(control);

        final FloatModelPropertyControl<PhysicsRigidBody> frictionControl =
                new FloatModelPropertyControl<>(friction, Messages.MODEL_PROPERTY_FRICTION, changeConsumer);

        frictionControl.setApplyHandler(PhysicsRigidBody::setFriction);
        frictionControl.setSyncHandler(PhysicsRigidBody::getFriction);
        frictionControl.setMinMax(0F, Integer.MAX_VALUE);
        frictionControl.setScrollPower(10F);
        frictionControl.setEditObject(control);

        final FloatModelPropertyControl<PhysicsRigidBody> linearDampingControl =
                new FloatModelPropertyControl<>(linearDamping, Messages.MODEL_PROPERTY_LINEAR_DAMPING, changeConsumer);

        linearDampingControl.setApplyHandler(PhysicsRigidBody::setLinearDamping);
        linearDampingControl.setSyncHandler(PhysicsRigidBody::getLinearDamping);
        linearDampingControl.setMinMax(0F, 1F);
        linearDampingControl.setScrollPower(1F);
        linearDampingControl.setEditObject(control);

        FloatModelPropertyControl<PhysicsRigidBody> massControl = null;

        if (control.getMass() != 0F) {

            massControl = new FloatModelPropertyControl<>(mass, Messages.MODEL_PROPERTY_MASS, changeConsumer);

            massControl.setApplyHandler(PhysicsRigidBody::setMass);
            massControl.setSyncHandler(PhysicsRigidBody::getMass);
            massControl.setMinMax(0.0001F, Integer.MAX_VALUE);
            massControl.setScrollPower(1F);
            massControl.setEditObject(control);
        }

        final FloatModelPropertyControl<PhysicsRigidBody> restitutionControl =
                new FloatModelPropertyControl<>(restitution, Messages.MODEL_PROPERTY_RESTITUTION, changeConsumer);

        restitutionControl.setApplyHandler(PhysicsRigidBody::setRestitution);
        restitutionControl.setSyncHandler(PhysicsRigidBody::getRestitution);
        restitutionControl.setEditObject(control);

        FXUtils.addToPane(kinematicControl, container);
        FXUtils.addToPane(angularDampingControl, container);
        FXUtils.addToPane(angularFactorControl, container);
        FXUtils.addToPane(angularSleepingThresholdControl, container);
        FXUtils.addToPane(frictionControl, container);
        FXUtils.addToPane(linearDampingControl, container);

        if (massControl != null) {
            FXUtils.addToPane(massControl, container);
        }

        FXUtils.addToPane(restitutionControl, container);

        buildSplitLine(container);

        FXUtils.addToPane(angularVelocityControl, container);
        FXUtils.addToPane(gravityControl, container);
        FXUtils.addToPane(linearFactorControl, container);
    }
}
