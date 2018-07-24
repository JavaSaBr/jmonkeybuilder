package com.ss.editor.model.undo.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
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
    private volatile VehicleWheel wheel;

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

    public RemoveVehicleWheelOperation(@NotNull VehicleControl control, @NotNull VehicleWheel wheel) {
        this.control = control;
        this.connectionPoint = wheel.getLocation();
        this.direction = wheel.getDirection();
        this.axle = wheel.getAxle();
        this.restLength = wheel.getRestLength();
        this.wheelRadius = wheel.getRadius();
        this.isFrontWheel = wheel.isFrontWheel();
        this.wheel = wheel;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {

        super.redoInJme(editor);

        for (int i = 0, length = control.getNumWheels(); i < length; i++) {
            var wheel = control.getWheel(i);
            if (wheel == this.wheel) {
                control.removeWheel(i);
                break;
            }
        }
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxRemovedChild(control, notNull(wheel));
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        this.wheel = control.addWheel(connectionPoint, direction, axle, restLength,
                wheelRadius, isFrontWheel);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxAddedChild(control, notNull(wheel), -1, false);
    }
}
