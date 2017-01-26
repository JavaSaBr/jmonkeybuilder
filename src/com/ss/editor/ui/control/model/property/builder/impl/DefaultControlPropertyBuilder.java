package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for default controls.
 *
 * @author JavaSaBr
 */
public class DefaultControlPropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    private static final PropertyBuilder INSTANCE = new DefaultControlPropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    public DefaultControlPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof Control)) return;

        if (object instanceof AbstractControl) {

            final AbstractControl control = (AbstractControl) object;

            final boolean enabled = control.isEnabled();

            final BooleanModelPropertyControl<AbstractControl> enabledControl =
                    new BooleanModelPropertyControl<>(enabled, "Enabled", changeConsumer);

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
        }
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

        final BooleanModelPropertyControl<RigidBodyControl> kinematicSpatialControl =
                new BooleanModelPropertyControl<>(kinematicSpatial, "Kinematic spatial", changeConsumer);

        kinematicSpatialControl.setApplyHandler(RigidBodyControl::setKinematicSpatial);
        kinematicSpatialControl.setSyncHandler(RigidBodyControl::isKinematicSpatial);
        kinematicSpatialControl.setEditObject(control);

        final BooleanModelPropertyControl<RigidBodyControl> kinematicControl =
                new BooleanModelPropertyControl<>(kinematic, "Kinematic", changeConsumer);

        kinematicControl.setApplyHandler(RigidBodyControl::setKinematic);
        kinematicControl.setSyncHandler(RigidBodyControl::isKinematic);
        kinematicControl.setEditObject(control);

        final Vector3fModelPropertyControl<RigidBodyControl> angularVelocityControl =
                new Vector3fModelPropertyControl<>(angularVelocity, "Angular velocity", changeConsumer);

        angularVelocityControl.setApplyHandler(RigidBodyControl::setAngularVelocity);
        angularVelocityControl.setSyncHandler(RigidBodyControl::getAngularVelocity);
        angularVelocityControl.setEditObject(control);

        final Vector3fModelPropertyControl<RigidBodyControl> gravityControl =
                new Vector3fModelPropertyControl<>(gravity, "Gravity", changeConsumer);

        gravityControl.setApplyHandler(RigidBodyControl::setGravity);
        gravityControl.setSyncHandler(RigidBodyControl::getGravity);
        gravityControl.setEditObject(control);

        final Vector3fModelPropertyControl<RigidBodyControl> linearFactorControl =
                new Vector3fModelPropertyControl<>(linearFactor, "Linear factor", changeConsumer);

        linearFactorControl.setApplyHandler(RigidBodyControl::setLinearFactor);
        linearFactorControl.setSyncHandler(RigidBodyControl::getLinearFactor);
        linearFactorControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> angularDampingControl =
                new FloatModelPropertyControl<>(angularDamping, "Angular damping", changeConsumer);

        angularDampingControl.setApplyHandler(RigidBodyControl::setAngularDamping);
        angularDampingControl.setSyncHandler(PhysicsRigidBody::getAngularDamping);
        angularDampingControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> angularFactorControl =
                new FloatModelPropertyControl<>(angularFactor, "Angular factor", changeConsumer);

        angularFactorControl.setApplyHandler(RigidBodyControl::setAngularFactor);
        angularFactorControl.setSyncHandler(PhysicsRigidBody::getAngularFactor);
        angularFactorControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> angularSleepingThresholdControl =
                new FloatModelPropertyControl<>(angularSleepingThreshold, "Angular sleeping threshold", changeConsumer);

        angularSleepingThresholdControl.setApplyHandler(RigidBodyControl::setAngularSleepingThreshold);
        angularSleepingThresholdControl.setSyncHandler(PhysicsRigidBody::getAngularSleepingThreshold);
        angularSleepingThresholdControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> frictionControl =
                new FloatModelPropertyControl<>(friction, "Friction", changeConsumer);

        frictionControl.setApplyHandler(RigidBodyControl::setFriction);
        frictionControl.setSyncHandler(PhysicsRigidBody::getFriction);
        frictionControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> linearDampingControl =
                new FloatModelPropertyControl<>(linearDamping, "Linear damping", changeConsumer);

        linearDampingControl.setApplyHandler(RigidBodyControl::setLinearDamping);
        linearDampingControl.setSyncHandler(PhysicsRigidBody::getLinearDamping);
        linearDampingControl.setEditObject(control);

        final FloatModelPropertyControl<RigidBodyControl> massControl =
                new FloatModelPropertyControl<>(mass, "Mass", changeConsumer);

        massControl.setApplyHandler(RigidBodyControl::setMass);
        massControl.setSyncHandler(PhysicsRigidBody::getMass);

        final FloatModelPropertyControl<RigidBodyControl> restitutionControl =
                new FloatModelPropertyControl<>(restitution, "Restitution", changeConsumer);

        restitutionControl.setApplyHandler(RigidBodyControl::setRestitution);
        restitutionControl.setSyncHandler(PhysicsRigidBody::getRestitution);
        restitutionControl.setEditObject(control);

        FXUtils.addToPane(kinematicSpatialControl, container);
        FXUtils.addToPane(kinematicControl, container);
        FXUtils.addToPane(angularDampingControl, container);
        FXUtils.addToPane(angularFactorControl, container);
        FXUtils.addToPane(angularSleepingThresholdControl, container);
        FXUtils.addToPane(frictionControl, container);
        FXUtils.addToPane(linearDampingControl, container);
        FXUtils.addToPane(massControl, container);
        FXUtils.addToPane(restitutionControl, container);
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

        final Vector3fModelPropertyControl<CharacterControl> viewDirectionControl =
                new Vector3fModelPropertyControl<>(viewDirection, "View direction", changeConsumer);

        viewDirectionControl.setApplyHandler(CharacterControl::setViewDirection);
        viewDirectionControl.setSyncHandler(CharacterControl::getViewDirection);
        viewDirectionControl.setEditObject(control);

        final Vector3fModelPropertyControl<CharacterControl> walkDirectionControl =
                new Vector3fModelPropertyControl<>(walkDirection, "Walk direction", changeConsumer);

        walkDirectionControl.setApplyHandler(CharacterControl::setWalkDirection);
        walkDirectionControl.setSyncHandler(CharacterControl::getWalkDirection);
        walkDirectionControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> fallSpeedControl =
                new FloatModelPropertyControl<>(fallSpeed, "Fall speed", changeConsumer);

        fallSpeedControl.setApplyHandler(CharacterControl::setFallSpeed);
        fallSpeedControl.setSyncHandler(CharacterControl::getFallSpeed);
        fallSpeedControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> gravityControl =
                new FloatModelPropertyControl<>(gravity, "Gravity", changeConsumer);

        gravityControl.setApplyHandler(CharacterControl::setGravity);
        gravityControl.setSyncHandler(CharacterControl::getGravity);
        gravityControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> jumpSpeedControl =
                new FloatModelPropertyControl<>(jumpSpeed, "Jump speed", changeConsumer);

        jumpSpeedControl.setApplyHandler(CharacterControl::setJumpSpeed);
        jumpSpeedControl.setSyncHandler(CharacterControl::getJumpSpeed);
        jumpSpeedControl.setEditObject(control);

        final FloatModelPropertyControl<CharacterControl> maxSlopeControl =
                new FloatModelPropertyControl<>(maxSlope, "Max slope", changeConsumer);

        maxSlopeControl.setApplyHandler(CharacterControl::setMaxSlope);
        maxSlopeControl.setSyncHandler(CharacterControl::getMaxSlope);
        maxSlopeControl.setEditObject(control);

        final BooleanModelPropertyControl<CharacterControl> applyPhysicsLocalControl =
                new BooleanModelPropertyControl<>(applyPhysicsLocal, "Apply physics local", changeConsumer);

        applyPhysicsLocalControl.setApplyHandler(CharacterControl::setApplyPhysicsLocal);
        applyPhysicsLocalControl.setSyncHandler(CharacterControl::isApplyPhysicsLocal);
        applyPhysicsLocalControl.setEditObject(control);

        final BooleanModelPropertyControl<CharacterControl> useViewDirectionControl =
                new BooleanModelPropertyControl<>(useViewDirection, "Use view direction", changeConsumer);

        useViewDirectionControl.setApplyHandler(CharacterControl::setUseViewDirection);
        useViewDirectionControl.setSyncHandler(CharacterControl::isUseViewDirection);
        useViewDirectionControl.setEditObject(control);

        FXUtils.addToPane(applyPhysicsLocalControl, container);
        FXUtils.addToPane(useViewDirectionControl, container);
        FXUtils.addToPane(fallSpeedControl, container);
        FXUtils.addToPane(gravityControl, container);
        FXUtils.addToPane(jumpSpeedControl, container);
        FXUtils.addToPane(maxSlopeControl, container);
        FXUtils.addToPane(viewDirectionControl, container);
        FXUtils.addToPane(walkDirectionControl, container);
    }

    private void build(final @NotNull SkeletonControl control, final @NotNull VBox container,
                       final @NotNull ModelChangeConsumer changeConsumer) {

        final boolean hardwareSkinningPreferred = control.isHardwareSkinningPreferred();

        final BooleanModelPropertyControl<SkeletonControl> hardwareSkinningPreferredControl =
                new BooleanModelPropertyControl<>(hardwareSkinningPreferred, "Hardware skinning preferred", changeConsumer);

        hardwareSkinningPreferredControl.setApplyHandler(SkeletonControl::setHardwareSkinningPreferred);
        hardwareSkinningPreferredControl.setSyncHandler(SkeletonControl::isHardwareSkinningPreferred);
        hardwareSkinningPreferredControl.setEditObject(control);

        FXUtils.addToPane(hardwareSkinningPreferredControl, container);
    }
}
