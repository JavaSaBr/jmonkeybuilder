package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The operation to change a mesh of a model.
 *
 * @author JavaSaBr
 */
public class ChangeMeshOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The new mesh.
     */
    @NotNull
    private final Mesh newMesh;

    /**
     * The previous mesh.
     */
    @NotNull
    private final Mesh oldMesh;

    /**
     * The geometry.
     */
    @NotNull
    private final Geometry geometry;

    /**
     * Instantiates a new Change mesh operation.
     *
     * @param newMesh  the new mesh
     * @param oldMesh  the old mesh
     * @param geometry the geometry
     */
    public ChangeMeshOperation(@NotNull final Mesh newMesh, @NotNull final Mesh oldMesh, @NotNull final Geometry geometry) {
        this.newMesh = newMesh;
        this.oldMesh = oldMesh;
        this.geometry = geometry;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            geometry.setMesh(newMesh);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(geometry, newMesh, "mesh"));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            geometry.setMesh(oldMesh);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(geometry, oldMesh, "mesh"));
        });
    }
}
