package com.ss.editor.control.transform;

/**
 * @author Ronn
 */
public class TransformConstraint {

    private static final float MOVE_CONSTRAINT, ROTATE_CONSTRAINT, SCALE_CONSTRAINT;

    static {
        MOVE_CONSTRAINT = 1;
        ROTATE_CONSTRAINT = 1;
        SCALE_CONSTRAINT = 1;
    }

    public static float constraintValue(float value, float constraintValue) {
        return value * constraintValue;
    }

    public static float getMoveConstraint() {
        return MOVE_CONSTRAINT;
    }

    public static float getRotateConstraint() {
        return ROTATE_CONSTRAINT;
    }

    public static float getScaleConstraint() {
        return SCALE_CONSTRAINT;
    }
}
