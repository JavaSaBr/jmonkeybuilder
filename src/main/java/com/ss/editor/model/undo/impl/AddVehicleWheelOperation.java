package com.ss.editor.model.undo.impl;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.rlib.common.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operation to add a wheel to a vehicle control.
 *
 * @author JavaSaBr
 */
public class AddVehicleWheelOperation extends AbstractEditorOperation<ModelChangeConsumer> {

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
    private volatile VehicleWheel createdWheel;

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

    public AddVehicleWheelOperation(
            @NotNull VehicleControl control,
            @NotNull Vector3f connectionPoint,
            @NotNull Vector3f direction,
            @NotNull Vector3f axle,
            float restLength,
            float wheelRadius,
            boolean isFrontWheel
    ) {
        this.control = control;
        this.connectionPoint = connectionPoint;
        this.direction = direction;
        this.axle = axle;
        this.restLength = restLength;
        this.wheelRadius = wheelRadius;
        this.isFrontWheel = isFrontWheel;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        this.createdWheel = control.addWheel(connectionPoint, direction, axle, restLength,
                wheelRadius, isFrontWheel);
    }

    @Override
    @FxThread
    protected void finishRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishRedoInFx(editor);
        editor.notifyFxAddedChild(control, ObjectUtils.notNull(createdWheel), -1, true);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);

        for (int i = 0, length = control.getNumWheels(); i < length; i++) {
            if (control.getWheel(i) == createdWheel) {
                control.removeWheel(i);
                break;
            }
        }
    }

    @Override
    @FxThread
    protected void finishUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishUndoInFx(editor);
        editor.notifyFxRemovedChild(control, ObjectUtils.notNull(createdWheel));
        createdWheel = null;
    }
}
