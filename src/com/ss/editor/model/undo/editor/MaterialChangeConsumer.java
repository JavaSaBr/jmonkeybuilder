package com.ss.editor.model.undo.editor;

import com.jme3.material.Material;
import com.ss.editor.annotation.FXThread;

import org.jetbrains.annotations.NotNull;

/**
 * The interface to notify about any changes of materials.
 *
 * @author JavaSaBr
 */
public interface MaterialChangeConsumer {

    /**
     * Gets current material.
     *
     * @return the current material.
     */
    @NotNull
    @FXThread
    Material getCurrentMaterial();

    /**
     * Notify about a changed parameter.
     *
     * @param paramName the param name
     */
    @FXThread
    void notifyChangeParam(@NotNull final String paramName);

    /**
     * Notify about changed render state.
     */
    @FXThread
    void notifyChangedRenderState();
}
