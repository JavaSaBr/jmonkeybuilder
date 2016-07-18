package com.ss.editor.control.transform;

/**
 * @author Ronn
 */
public class TransformConstraint {

    private static final float MOVE_CONSTRAINT, ROTATE_CONSTRAINT, SCALE_CONSTRAINT;

    static {
        MOVE_CONSTRAINT = 10;
        ROTATE_CONSTRAINT = 10;
        SCALE_CONSTRAINT = 10;
    }

    public static float constraintValue(float value, float constraintValue) {

        float theValue = value + 0f;
        float theConstraint = constraintValue + 0f;

        // fix
        if (constraintValue < 10f) {
            theValue *= 100f;
            theConstraint *= 100f;
        }

        if (theConstraint > 0f) {
            float rest = theValue % theConstraint;
            float valueToConstrait = theValue - rest;

            if (rest > theConstraint * 0.5f) {
                valueToConstrait += theConstraint;
            }

            // fix back
            if (constraintValue < 10f) {
                valueToConstrait /= 100f;
            }

            return valueToConstrait;

        } else {
            return value;
        }
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
