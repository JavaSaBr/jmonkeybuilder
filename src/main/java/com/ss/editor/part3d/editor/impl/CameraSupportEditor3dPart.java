package com.ss.editor.part3d.editor.impl;

import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.rlib.common.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to mark the editor d part support some additional camera.
 *
 * @author JavaSaBr
 */
public interface CameraSupportEditor3dPart extends Editor3dPart {

    /**
     * Get the editor camera.
     *
     * @return the editor camera.
     */
    @JmeThread
    @Nullable EditorCamera getEditorCamera() ;

    /**
     * Get the editor camera.
     *
     * @return the editor camera.
     */
    @JmeThread
    default @NotNull EditorCamera requireEditorCamera() {
        return ObjectUtils.notNull(getEditorCamera());
    }
}
