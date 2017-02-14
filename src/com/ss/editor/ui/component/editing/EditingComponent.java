package com.ss.editor.ui.component.editing;

import com.jme3.scene.control.Control;
import org.jetbrains.annotations.NotNull;
import rlib.util.HasName;

/**
 * The interface to implement editing component.
 *
 * @author JavaSaBr
 */
public interface EditingComponent extends HasName, Control {

    /**
     * Checks that an object can be edited using this component.
     *
     * @param object the object to check.
     * @return true if this object can be edited.
     */
    default boolean isSupport(@NotNull Object object) {
        return false;
    }
}
