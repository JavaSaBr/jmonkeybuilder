package com.ss.editor.model.undo.impl;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

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

    public ChangeMeshOperation(@NotNull Mesh newMesh, @NotNull Mesh oldMesh, @NotNull Geometry geometry) {
        this.newMesh = newMesh;
        this.oldMesh = oldMesh;
        this.geometry = geometry;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        geometry.setMesh(newMesh);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxChangeProperty(geometry, newMesh, "mesh");
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        geometry.setMesh(oldMesh);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxChangeProperty(geometry, oldMesh, "mesh");
    }
}
