package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link AbstractEditorOperation} for changing a shape in the {@link
 * ParticleEmitterNode}.
 *
 * @author JavaSaBr.
 */
public class ChangeEmitterShapeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The index of emitter node.
     */
    private final int index;

    /**
     * The prevShape shape.
     */
    @NotNull
    private volatile Mesh prevShape;

    public ChangeEmitterShapeOperation(@NotNull final Mesh newShape, final int index) {
        this.prevShape = newShape;
        this.index = index;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof ParticleEmitterNode)) return;

            final ParticleEmitterNode node = (ParticleEmitterNode) parent;
            final EmitterMesh emitterMesh = node.getEmitterShape();
            final Mesh newShape = prevShape;
            prevShape = emitterMesh.getMesh();
            node.setEmitterShapeMesh(newShape);

            //TODO надо добавить отдельное понятие таких штук EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(node, emitterNode));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof ParticleEmitterNode)) return;

            final ParticleEmitterNode node = (ParticleEmitterNode) parent;
            final EmitterMesh emitterMesh = node.getEmitterShape();
            final Mesh newShape = prevShape;
            prevShape = emitterMesh.getMesh();
            node.setEmitterShapeMesh(newShape);

            //TODO надо добавить отдельное понятие таких штук EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(node, emitterNode));
        });
    }
}
