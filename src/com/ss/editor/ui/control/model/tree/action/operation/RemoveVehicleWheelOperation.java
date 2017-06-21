package com.ss.editor.ui.control.model.tree.action.operation;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operation to remove a wheel from a vehicle control.
 *
 * @author JavaSaBr
 */
public class RemoveVehicleWheelOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The vehicle control.
     */
    @NotNull
    private final VehicleControl control;

    /**
     * The connection point.
     */
    @NotNull
    private final Vector3f connectionPoint;

    /**
     * The direction.
     */
    @NotNull
    private final Vector3f direction;

    /**
     * The axle.
     */
    @NotNull
    private final Vector3f axle;

    /**
     * The wheel.
     */
    @Nullable
    private VehicleWheel createdWheel;

    /**
     * The rest length.
     */
    private final float restLength;

    /**
     * The wheel radius.
     */
    private final float wheelRadius;

    /**
     * The flag is front wheel.
     */
    private final boolean isFrontWheel;

    /**
     * Instantiates a new Remove vehicle wheel operation.
     *
     * @param control the control
     * @param wheel   the wheel
     */
    public RemoveVehicleWheelOperation(@NotNull final VehicleControl control, @NotNull final VehicleWheel wheel) {
        this.control = control;
        this.connectionPoint = wheel.getLocation();
        this.direction = wheel.getDirection();
        this.axle = wheel.getAxle();
        this.restLength = wheel.getRestLength();
        this.wheelRadius = wheel.getRadius();
        this.isFrontWheel = wheel.isFrontWheel();
        this.createdWheel = wheel;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            for (int i = 0, length = control.getNumWheels(); i < length; i++) {
                final VehicleWheel wheel = control.getWheel(i);
                if (wheel == createdWheel) {
                    control.removeWheel(i);
                    break;
                }
            }

            final VehicleWheel toRemove = requireNonNull(createdWheel);

            this.createdWheel = null;

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(control, toRemove));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final VehicleWheel vehicleWheel = control.addWheel(connectionPoint, direction, axle, restLength,
                    wheelRadius, isFrontWheel);

            this.createdWheel = vehicleWheel;

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(control, vehicleWheel, -1));
        });
    }
}
