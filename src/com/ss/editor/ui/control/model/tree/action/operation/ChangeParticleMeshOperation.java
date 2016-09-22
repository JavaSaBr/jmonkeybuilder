package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataMeshInfo;

/**
 * The implementation of the {@link AbstractEditorOperation} for changing a particle mesh in the
 * {@link ParticleGeometry}.
 *
 * @author JavaSaBr.
 */
public class ChangeParticleMeshOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The index of emitter node.
     */
    private final int index;

    /**
     * The prevInfo shape.
     */
    @NotNull
    private volatile ParticleDataMeshInfo prevInfo;

    public ChangeParticleMeshOperation(@NotNull final ParticleDataMeshInfo newInfo, final int index) {
        this.prevInfo = newInfo;
        this.index = index;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> switchInfo(editor));
    }

    private void switchInfo(final @NotNull ModelChangeConsumer editor) {

        final Spatial currentModel = editor.getCurrentModel();
        final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
        if (!(parent instanceof ParticleGeometry)) return;

        final ParticleGeometry geometry = (ParticleGeometry) parent;
        final ParticleEmitterNode emitterNode = (ParticleEmitterNode) NodeUtils.findParent(geometry, spatial -> spatial instanceof ParticleEmitterNode);
        if (emitterNode == null) return;

        final ParticleDataMeshInfo newInfo = prevInfo;
        prevInfo = emitterNode.getParticleMeshType();
        emitterNode.changeParticleMeshType(newInfo);

        //TODO надо добавить отдельное понятие таких штук EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(node, emitterNode));
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> switchInfo(editor));
    }
}
