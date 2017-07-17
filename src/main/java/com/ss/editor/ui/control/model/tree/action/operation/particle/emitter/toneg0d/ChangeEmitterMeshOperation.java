package com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d;

import com.jme3.scene.Mesh;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link AbstractEditorOperation} for changing a shape in the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr.
 */
public class ChangeEmitterMeshOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The emitter node.
     */
    @NotNull
    private final ParticleEmitterNode emitterNode;

    /**
     * The prev shape.
     */
    @NotNull
    private volatile Mesh prevShape;

    /**
     * Instantiates a new Change toneg 0 d particle emitter shape operation.
     *
     * @param newShape    the new shape
     * @param emitterNode the emitter node
     */
    public ChangeEmitterMeshOperation(@NotNull final Mesh newShape, @NotNull final ParticleEmitterNode emitterNode) {
        this.prevShape = newShape;
        this.emitterNode = emitterNode;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> switchShape(editor));
    }

    private void switchShape(final @NotNull ModelChangeConsumer editor) {

        final EmitterMesh emitterMesh = emitterNode.getEmitterShape();
        final Mesh newShape = prevShape;
        prevShape = emitterMesh.getMesh();
        emitterNode.changeEmitterShapeMesh(newShape);

        EXECUTOR_MANAGER.addFXTask(() -> editor.notifyReplaced(emitterNode, emitterMesh, emitterMesh));
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> switchShape(editor));
    }
}
