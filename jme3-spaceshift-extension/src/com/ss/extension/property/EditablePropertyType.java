package com.ss.extension.property;

/**
 * The enum with list editable property types.
 *
 * @author JavaSaBr
 */
public enum EditablePropertyType {
    BOOLEAN,
    INTEGER,
    FLOAT,
    VECTOR_2F,
    VECTOR_3F,
    QUATERNION,
    COLOR,
    ENUM,
    DIRECTION_LIGHT_FROM_SCENE,
    POINT_LIGHT_FROM_SCENE;

    private static final EditablePropertyType[] TYPES = values();

    public static EditablePropertyType valueOf(final int index) {
        return TYPES[index];
    }
}
