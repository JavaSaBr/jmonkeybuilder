package com.ss.editor.util;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.control.editing.EditingControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The utility class.
 *
 * @author JavaSaBr
 */
public class EditingUtils {

    /**
     * Get an editing control of a cursor node.
     *
     * @param cursorNode the cursor node.
     * @return the editing control or null.
     */
    @Nullable
    public static EditingControl getEditingControl(@NotNull final Node cursorNode) {
        return cursorNode.getControl(EditingControl.class);
    }

    /**
     * Get an edit model of a cursor node.
     *
     * @param cursorNode the cursor node.
     * @return the edited model or null.
     */
    @Nullable
    public static Spatial getEditedModel(@NotNull final Node cursorNode) {
        final EditingControl control = getEditingControl(cursorNode);
        return control == null ? null : control.getEditedModel();
    }

    /**
     * Get an edit model.
     *
     * @param control the editing control.
     * @return the edited model or null.
     */
    @Nullable
    public static Spatial getEditedModel(@Nullable final EditingControl control) {
        return control == null ? null : control.getEditedModel();
    }
}
