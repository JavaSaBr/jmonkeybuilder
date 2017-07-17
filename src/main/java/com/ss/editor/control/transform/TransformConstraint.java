package com.ss.editor.control.transform;

/**
 * The type Transform constraint.
 *
 * @author JavaSaBr
 */
class TransformConstraint {

    private static final float MOVE_CONSTRAINT, ROTATE_CONSTRAINT, SCALE_CONSTRAINT;

    static {
        MOVE_CONSTRAINT = 1;
        ROTATE_CONSTRAINT = 1;
        SCALE_CONSTRAINT = 1;
    }

    /**
     * Constraint value float.
     *
     * @param value           the value
     * @param constraintValue the constraint value
     * @return the float
     */
    static float constraintValue(float value, float constraintValue) {
        return value * constraintValue;
    }

    /**
     * Gets move constraint.
     *
     * @return the move constraint
     */
    static float getMoveConstraint() {
        return MOVE_CONSTRAINT;
    }

    /**
     * Gets rotate constraint.
     *
     * @return the rotate constraint
     */
    static float getRotateConstraint() {
        return ROTATE_CONSTRAINT;
    }

    /**
     * Gets scale constraint.
     *
     * @return the scale constraint
     */
    static float getScaleConstraint() {
        return SCALE_CONSTRAINT;
    }
}
