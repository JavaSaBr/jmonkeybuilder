package com.ss.editor.ui.control.model.property.generic;

/**
 * The enum with list editable property types.
 *
 * @author JavaSaBr
 */
public enum EditablePropertyType {
    BOOLEAN,;

    private static final EditablePropertyType[] TYPES = values();

    public static EditablePropertyType valueOf(final int index) {
        return TYPES[index];
    }
}
