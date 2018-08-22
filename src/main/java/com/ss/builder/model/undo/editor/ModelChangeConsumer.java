package com.ss.builder.model.undo.editor;

import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to notify about any changes of models.
 *
 * @author JavaSaBr
 */
public interface ModelChangeConsumer extends ChangeConsumer {

    /**
     * Get the current model.
     *
     * @return the current model of the editor.
     */
    @FxThread
    @NotNull Spatial getCurrentModel();
}
