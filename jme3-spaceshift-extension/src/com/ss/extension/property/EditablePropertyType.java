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
    ENUM,;

    private static final EditablePropertyType[] TYPES = values();

    public static EditablePropertyType valueOf(final int index) {
        return TYPES[index];
    }
}
