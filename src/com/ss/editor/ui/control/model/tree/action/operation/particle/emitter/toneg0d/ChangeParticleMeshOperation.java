package com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d;

import static com.ss.editor.util.NodeUtils.findParent;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataMeshInfo;

/**
 * The implementation of the {@link AbstractEditorOperation} for changing a particle mesh in the {@link
 * ParticleGeometry}*.
 *
 * @author JavaSaBr.
 */
public class ChangeParticleMeshOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The geometry.
     */
    @NotNull
    private final ParticleGeometry geometry;

    /**
     * The prev shape.
     */
    @NotNull
    private volatile ParticleDataMeshInfo prevInfo;

    /**
     * Instantiates a new Change particle mesh operation.
     *
     * @param newInfo  the new info
     * @param geometry the geometry
     */
    public ChangeParticleMeshOperation(@NotNull final ParticleDataMeshInfo newInfo, @NotNull final ParticleGeometry geometry) {
        this.prevInfo = newInfo;
        this.geometry = geometry;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> switchInfo(editor));
    }

    private void switchInfo(final @NotNull ModelChangeConsumer editor) {

        final ParticleEmitterNode emitterNode = findParent(geometry, spatial -> spatial instanceof ParticleEmitterNode);
        if (emitterNode == null) return;

        final ParticleDataMeshInfo newInfo = prevInfo;
        prevInfo = emitterNode.getParticleMeshType();
        emitterNode.changeParticleMeshType(newInfo);
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> switchInfo(editor));
    }
}
