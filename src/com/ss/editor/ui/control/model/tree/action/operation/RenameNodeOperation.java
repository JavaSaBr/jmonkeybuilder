package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The operation to rename a node.
 *
 * @author JavaSaBr
 */
public class RenameNodeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The constant PROPERTY_NAME.
     */
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

    /**
     * Instantiates a new Rename node operation.
     *
     * @param oldName the old name
     * @param newName the new name
     * @param spatial the spatial
     */
    public RenameNodeOperation(@NotNull final String oldName, @NotNull final String newName, @NotNull final Spatial spatial) {
        this.oldName = oldName;
        this.newName = newName;
        this.spatial = spatial;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            spatial.setName(newName);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(spatial.getParent(), spatial, PROPERTY_NAME));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            spatial.setName(oldName);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(spatial.getParent(), spatial, PROPERTY_NAME));
        });
    }
}
