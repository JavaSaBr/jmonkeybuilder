package com.ss.editor.part3d.editor;

import com.ss.editor.annotation.JmeThread;

/**
 * The interface to mark an editor 3d part that it supports a save method.
 *
 * @author JavaSaBr
 */
public interface SavableEditor3dPart extends Editor3dPart {

    /**
     * Save changes.
     */
    @JmeThread
    void save();

    /**
     * Return true if this editor part has unsaved changes.
     *
     * @return true if this editor part has unsaved changes.
     */
    @JmeThread
    boolean isDirty();
}
