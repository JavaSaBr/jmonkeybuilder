package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.cinematic.events.AbstractCinematicEvent;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.EnumModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.QuaternionModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for default controls.
 *
 * @author JavaSaBr
 */
public class DefaultControlPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    private static final PropertyBuilder INSTANCE = new DefaultControlPropertyBuilder();

    private static final MotionEvent.Direction[] DIRECTIONS = MotionEvent.Direction.values();
    private static final LoopMode[] LOOP_MODES = LoopMode.values();

    private static final BiConsumer<MotionEvent, Boolean> MOTION_EVENT_APPLY_HANDLER = (motionEvent, value) -> {
        if (value) {
            motionEvent.play();
        } else {
            motionEvent.stop();
        }
    };

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    public DefaultControlPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        if (object instanceof AbstractCinematicEvent) {

            final AbstractCinematicEvent control = (AbstractCinematicEvent) object;

            final LoopMode loopMode = control.getLoopMode();

            final float initialDuration = control.getInitialDuration();
            final float speed = control.getSpeed();
            final float time = control.getTime();

            final EnumModelPropertyControl<AbstractCinematicEvent, LoopMode> loopModeControl =
                    new EnumModelPropertyControl<>(loopMode, Messages.CONTROL_PROPERTY_LOOP_MODE, changeConsumer, LOOP_MODES);

            loopModeControl.setApplyHandler(AbstractCinematicEvent::setLoopMode);
            loopModeControl.setSyncHandler(AbstractCinematicEvent::getLoopMode);
            loopModeControl.setEditObject(control);

            final FloatModelPropertyControl<AbstractCinematicEvent> initialDurationControl =
                    new FloatModelPropertyControl<>(initialDuration, Messages.CONTROL_PROPERTY_INITIAL_DURATION, changeConsumer);

            initialDurationControl.setApplyHandler(AbstractCinematicEvent::setInitialDuration);
            initialDurationControl.setSyncHandler(AbstractCinematicEvent::getInitialDuration);
            initialDurationControl.setEditObject(control);

            final FloatModelPropertyControl<AbstractCinematicEvent> speedControl =
                    new FloatModelPropertyControl<>(speed, Messages.CONTROL_PROPERTY_SPEED, changeConsumer);

            speedControl.setApplyHandler(AbstractCinematicEvent::setSpeed);
            speedControl.setSyncHandler(AbstractCinematicEvent::getSpeed);
            speedControl.setEditObject(control);

            final FloatModelPropertyControl<AbstractCinematicEvent> timeControl =
                    new FloatModelPropertyControl<>(time, Messages.CONTROL_PROPERTY_TIME, changeConsumer);

            timeControl.setApplyHandler(AbstractCinematicEvent::setSpeed);
            timeControl.setSyncHandler(AbstractCinematicEvent::getSpeed);
            timeControl.setEditObject(control);

            FXUtils.addToPane(loopModeControl, container);
            FXUtils.addToPane(initialDurationControl, container);
            FXUtils.addToPane(speedControl, container);
            FXUtils.addToPane(timeControl, container);
        }

        if (!(object instanceof Control)) return;

        if (object instanceof AbstractControl) {

            final AbstractControl control = (AbstractControl) object;

            final boolean enabled = control.isEnabled();

            final BooleanModelPropertyControl<AbstractControl> enabledControl =
                    new BooleanModelPropertyControl<>(enabled, Messages.CONTROL_PROPERTY_ENABLED, changeConsumer);

            enabledControl.setApplyHandler(AbstractControl::setEnabled);
            enabledControl.setSyncHandler(AbstractControl::isEnabled);
            enabledControl.setEditObject(control);

            FXUtils.addToPane(enabledControl, container);
        }

        if (object instanceof SkeletonControl) {
            build((SkeletonControl) object, container, changeConsumer);
        } else if (object instanceof CharacterControl) {
            build((CharacterControl) object, container, changeConsumer);
        } else if (object instanceof RigidBodyControl) {
            build((RigidBodyControl) object, container, changeConsumer);
        } else if (object instanceof MotionEvent) {
            build((MotionEvent) object, container, changeConsumer);
        }
    }

    private void build(final @NotNull MotionEvent control, @NotNull final VBox container,
                       final @NotNull ModelChangeConsumer changeConsumer) {

        final Vector3f direction = control.getDirection();
        final Quaternion rotation = control.getRotation();

        final MotionEvent.Direction directionType = control.getDirectionType();

        final EnumModelPropertyControl<MotionEvent, MotionEvent.Direction> directionTypeControl =
                new EnumModelPropertyControl<>(directionType, Messages.CONTROL_PROPERTY_DIRECTION_TYPE, changeConsumer, DIRECTIONS);

        directionTypeControl.setApplyHandler(MotionEvent::setDirectionType);
        directionTypeControl.setSyncHandler(MotionEvent::getDirectionType);
        directionTypeControl.setEditObject(control);

        final Vector3fModelPropertyControl<MotionEvent> directionControl =
                new Vector3fModelPropertyControl<>(direction, Messages.CONTROL_PROPERTY_DIRECTION, changeConsumer);

        directionControl.setApplyHandler(MotionEvent::setDirection);
        directionControl.setSyncHandler(MotionEvent::getDirection);
        directionControl.setEditObject(control);

        final QuaternionModelPropertyControl<MotionEvent> rotationControl =
                new QuaternionModelPropertyControl<>(rotation, Messages.CONTROL_PROPERTY_ROTATION, changeConsumer);

        rotationControl.setApplyHandler(MotionEvent::setRotation);
        rotationControl.setSyncHandler(MotionEvent::getRotation);
        rotationControl.setEditObject(control);

        FXUtils.addToPane(directionTypeControl, container);

        addSplitLine(container);

        FXUtils.addToPane(directionControl, container);
        FXUtils.addToPane(rotationControl, container);
    }

    private void build(final @NotNull RigidBodyControl control, final @NotNull VBox container,
                       final @NotNull ModelChangeConsumer changeConsumer) {

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

        final boolean kinematicSpatial = control.isKinematicSpatial();
        final boolean kinematic = control.isKinematic();
        final boolean enabled = control.isEnabled();

        final BooleanModelPropertyControl<RigidBodyControl> enabledControl =
                new BooleanModelPropertyControl<>(enabled, Messages.CONTROL_PROPERTY_ENABLED, changeConsumer);

        enabledControl.setApplyHandler(RigidBodyControl::setEnabled);
        enabledControl.setSyncHandler(RigidBodyControl::isEnabled);
        enabledControl.setEditObject(control);

        final BooleanModelPropertyControl<RigidBodyControl> kinematicSpatialControl =
                new BooleanModelPropertyControl<>(kinematicSpatial, Messages.CONTROL_PROPERTY_KINEMATIC_SPATIAL, changeConsumer);

        kinematicSpatialControl.setApplyHandler(RigidBodyControl::setKinematicSpatial);
        kinematicSpatialControl.setSyncHandler(RigidBodyControl::isKinematicSpatial);
        kinematicSpatialControl.setEditObject(control);

        final BooleanModelPropertyControl<RigidBodyControl> kinematicControl =
                new BooleanModelPropertyControl<>(kinematic, Messages.CONTROL_PROPERTY_KINEMATIC, changeConsumer);

        kinematicControl.setApplyHandler(RigidBodyControl::setKinematic);
        kinematicControl.setSyncHandler(RigidBodyControl::isKinematic);
        kinematicControl.setEditObject(control);

        final Vector3fModelPropertyControl<RigidBodyControl> angularVelocityControl =
                new Vector3fModelPropertyControl<>(angularVelocity, Messages.CONTROL_PROPERTY_ANGULAR_VELOCITY, changeConsumer);

        angularVelocityControl.setApplyHandler(RigidBodyControl::setAngularVelocity);
        angularVelocityControl.setSyncHandler(RigidBodyControl::getAngularVelocity);
        angularVelocityControl.setEditObject(control);

        final Vector3fModelPropertyControl<RigidBodyControl> gravityControl =
                new Vector3fModelPropertyControl<>(gravity, Messages.CONTROL_PROPERTY_GRAVITY, changeConsumer);

        gravityControl.setApplyHandler(RigidBodyControl::setGravity);
        gravityControl.setSyncHandler(RigidBodyControl::getGravity);
        gravityControl.setEditObject(control);

        final Vector3fModelPropertyControl<RigidBodyControl> linearFactorControl =
                new Vector3fModelPropertyControl<>(linearFactor, Messages.CONTROL_PROPERTY_LINEAR_FACTOR, changeConsumer);

        linearFactorControl.setApplyHandler(RigidBodyControl::setLinearFactor);
        linearFactorControl.setSyncHandler(RigidBodyControl::getLinearFactor);
        linearFactorControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> angularDampingControl =
                new FloatModelPropertyControl<>(angularDamping, Messages.CONTROL_PROPERTY_ANGULAR_DAMPING, changeConsumer);

        angularDampingControl.setApplyHandler(RigidBodyControl::setAngularDamping);
        angularDampingControl.setSyncHandler(PhysicsRigidBody::getAngularDamping);
        angularDampingControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> angularFactorControl =
                new FloatModelPropertyControl<>(angularFactor, Messages.CONTROL_PROPERTY_ANGULAR_FACTOR, changeConsumer);

        angularFactorControl.setApplyHandler(RigidBodyControl::setAngularFactor);
        angularFactorControl.setSyncHandler(PhysicsRigidBody::getAngularFactor);
        angularFactorControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> angularSleepingThresholdControl =
                new FloatModelPropertyControl<>(angularSleepingThreshold, Messages.CONTROL_PROPERTY_ANGULAR_SLEEPING_THRESHOLD, changeConsumer);

        angularSleepingThresholdControl.setApplyHandler(RigidBodyControl::setAngularSleepingThreshold);
        angularSleepingThresholdControl.setSyncHandler(PhysicsRigidBody::getAngularSleepingThreshold);
        angularSleepingThresholdControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> frictionControl =
                new FloatModelPropertyControl<>(friction, Messages.CONTROL_PROPERTY_FRICTION, changeConsumer);

        frictionControl.setApplyHandler(RigidBodyControl::setFriction);
        frictionControl.setSyncHandler(PhysicsRigidBody::getFriction);
        frictionControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> linearDampingControl =
                new FloatModelPropertyControl<>(linearDamping, Messages.CONTROL_PROPERTY_LINEAR_DAMPING, changeConsumer);

        linearDampingControl.setApplyHandler(RigidBodyControl::setLinearDamping);
        linearDampingControl.setSyncHandler(PhysicsRigidBody::getLinearDamping);
        linearDampingControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> massControl =
                new FloatModelPropertyControl<>(mass, Messages.CONTROL_PROPERTY_MASS, changeConsumer);

        massControl.setApplyHandler(RigidBodyControl::setMass);
        massControl.setSyncHandler(PhysicsRigidBody::getMass);
        massControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> restitutionControl =
                new FloatModelPropertyControl<>(restitution, Messages.CONTROL_PROPERTY_RESTITUTION, changeConsumer);

        restitutionControl.setApplyHandler(RigidBodyControl::setRestitution);
        restitutionControl.setSyncHandler(PhysicsRigidBody::getRestitution);
        restitutionControl.setEditObject(control);

        FXUtils.addToPane(enabledControl, container);
        FXUtils.addToPane(kinematicSpatialControl, container);
        FXUtils.addToPane(kinematicControl, container);
        FXUtils.addToPane(angularDampingControl, container);
        FXUtils.addToPane(angularFactorControl, container);
        FXUtils.addToPane(angularSleepingThresholdControl, container);
        FXUtils.addToPane(frictionControl, container);
        FXUtils.addToPane(linearDampingControl, container);
        FXUtils.addToPane(massControl, container);
        FXUtils.addToPane(restitutionControl, container);

        addSplitLine(container);

        FXUtils.addToPane(angularVelocityControl, container);
        FXUtils.addToPane(gravityControl, container);
        FXUtils.addToPane(linearFactorControl, container);
    }

    private void build(final @NotNull CharacterControl control, final @NotNull VBox container,
                       final @NotNull ModelChangeConsumer changeConsumer) {

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
                new BooleanModelPropertyControl<>(enabled, Messages.CONTROL_PROPERTY_ENABLED, changeConsumer);

        enabledControl.setApplyHandler(CharacterControl::setEnabled);
        enabledControl.setSyncHandler(CharacterControl::isEnabled);
        enabledControl.setEditObject(control);

        final Vector3fModelPropertyControl<CharacterControl> viewDirectionControl =
                new Vector3fModelPropertyControl<>(viewDirection, Messages.CONTROL_PROPERTY_VIEW_DIRECTION, changeConsumer);

        viewDirectionControl.setApplyHandler(CharacterControl::setViewDirection);
        viewDirectionControl.setSyncHandler(CharacterControl::getViewDirection);
        viewDirectionControl.setEditObject(control);

        final Vector3fModelPropertyControl<CharacterControl> walkDirectionControl =
                new Vector3fModelPropertyControl<>(walkDirection, Messages.CONTROL_PROPERTY_WALK_DIRECTION, changeConsumer);

        walkDirectionControl.setApplyHandler(CharacterControl::setWalkDirection);
        walkDirectionControl.setSyncHandler(CharacterControl::getWalkDirection);
        walkDirectionControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> fallSpeedControl =
                new FloatModelPropertyControl<>(fallSpeed, Messages.CONTROL_PROPERTY_FALL_SPEED, changeConsumer);

        fallSpeedControl.setApplyHandler(CharacterControl::setFallSpeed);
        fallSpeedControl.setSyncHandler(CharacterControl::getFallSpeed);
        fallSpeedControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> gravityControl =
                new FloatModelPropertyControl<>(gravity, Messages.CONTROL_PROPERTY_GRAVITY, changeConsumer);

        gravityControl.setApplyHandler(CharacterControl::setGravity);
        gravityControl.setSyncHandler(CharacterControl::getGravity);
        gravityControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> jumpSpeedControl =
                new FloatModelPropertyControl<>(jumpSpeed, Messages.CONTROL_PROPERTY_JUMP_SPEED, changeConsumer);

        jumpSpeedControl.setApplyHandler(CharacterControl::setJumpSpeed);
        jumpSpeedControl.setSyncHandler(CharacterControl::getJumpSpeed);
        jumpSpeedControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> maxSlopeControl =
                new FloatModelPropertyControl<>(maxSlope, Messages.CONTROL_PROPERTY_MAX_SLOPE, changeConsumer);

        maxSlopeControl.setApplyHandler(CharacterControl::setMaxSlope);
        maxSlopeControl.setSyncHandler(CharacterControl::getMaxSlope);
        maxSlopeControl.setEditObject(control);

        final BooleanModelPropertyControl<CharacterControl> applyPhysicsLocalControl =
                new BooleanModelPropertyControl<>(applyPhysicsLocal, Messages.CONTROL_PROPERTY_APPLY_PHYSICS_LOCAL, changeConsumer);

        applyPhysicsLocalControl.setApplyHandler(CharacterControl::setApplyPhysicsLocal);
        applyPhysicsLocalControl.setSyncHandler(CharacterControl::isApplyPhysicsLocal);
        applyPhysicsLocalControl.setEditObject(control);

        final BooleanModelPropertyControl<CharacterControl> useViewDirectionControl =
                new BooleanModelPropertyControl<>(useViewDirection, Messages.CONTROL_PROPERTY_USE_VIEW_DIRECTION, changeConsumer);

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

        addSplitLine(container);

        FXUtils.addToPane(viewDirectionControl, container);
        FXUtils.addToPane(walkDirectionControl, container);
    }

    private void build(final @NotNull SkeletonControl control, final @NotNull VBox container,
                       final @NotNull ModelChangeConsumer changeConsumer) {

        final boolean hardwareSkinningPreferred = control.isHardwareSkinningPreferred();

        final BooleanModelPropertyControl<SkeletonControl> hardwareSkinningPreferredControl =
                new BooleanModelPropertyControl<>(hardwareSkinningPreferred, Messages.CONTROL_PROPERTY_HARDWARE_SKINNING_PREFERRED, changeConsumer);

        hardwareSkinningPreferredControl.setApplyHandler(SkeletonControl::setHardwareSkinningPreferred);
        hardwareSkinningPreferredControl.setSyncHandler(SkeletonControl::isHardwareSkinningPreferred);
        hardwareSkinningPreferredControl.setEditObject(control);

        FXUtils.addToPane(hardwareSkinningPreferredControl, container);
    }
}
