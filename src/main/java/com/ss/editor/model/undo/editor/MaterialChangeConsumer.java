package com.ss.editor.model.undo.editor;

import com.jme3.material.Material;
import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to notify about any changes of materials.
 *
 * @author JavaSaBr
 */
public interface MaterialChangeConsumer extends ChangeConsumer{

    /**
     * Gets current material.
     *
     * @return the current material.
     */
    @FromAnyThread
    @NotNull Material getCurrentMaterial();
}
