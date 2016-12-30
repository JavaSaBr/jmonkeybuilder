package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

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
    private final Mesh newMesh;

    /**
     * The previous mesh.
     */
    private final Mesh oldMesh;

    /**
     * The index of geometry.
     */
    private final int index;

    public ChangeMeshOperation(final Mesh newMesh, final Mesh oldMesh, final int index) {
        this.newMesh = newMesh;
        this.oldMesh = oldMesh;
        this.index = index;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object object = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(object instanceof Geometry)) return;

            final Geometry geometry = (Geometry) object;
            geometry.setMesh(newMesh);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(object, object, "mesh"));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object object = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(object instanceof Geometry)) return;

            final Geometry geometry = (Geometry) object;
            geometry.setMesh(oldMesh);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(object, object, "mesh"));
        });
    }
}
