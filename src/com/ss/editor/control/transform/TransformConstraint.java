package com.ss.editor.control.transform;

/**
 * @author JavaSaBr
 */
class TransformConstraint {

    private static final float MOVE_CONSTRAINT, ROTATE_CONSTRAINT, SCALE_CONSTRAINT;

    static {
        MOVE_CONSTRAINT = 1;
        ROTATE_CONSTRAINT = 1;
        SCALE_CONSTRAINT = 1;
    }

    static float constraintValue(float value, float constraintValue) {
        return value * constraintValue;
    }

    static float getMoveConstraint() {
        return MOVE_CONSTRAINT;
    }

    static float getRotateConstraint() {
        return ROTATE_CONSTRAINT;
    }

    static float getScaleConstraint() {
        return SCALE_CONSTRAINT;
    }
}
