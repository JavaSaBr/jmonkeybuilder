package com.ss.editor.model.undo.impl;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The operation to rename a node.
 *
 * @author JavaSaBr
 */
public class RenameNodeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    public static final String PROPERTY_NAME = "name";

    /**
     * The old name.
     */
    @NotNull
    private final String oldName;

    /**
     * The new name.
     */
    @NotNull
    private final String newName;

    /**
     * The node.
     */
    @NotNull
    private final Spatial spatial;

    public RenameNodeOperation(@NotNull final String oldName, @NotNull final String newName, @NotNull final Spatial spatial) {
        this.oldName = oldName;
        this.newName = newName;
        this.spatial = spatial;
    }

    @Override
    protected void redoInFx(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            spatial.setName(newName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(spatial, PROPERTY_NAME));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            spatial.setName(oldName);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangeProperty(spatial, PROPERTY_NAME));
        });
    }
}
