package com.ss.editor.ui.control.model.tree.action.operation;

import com.jme3.scene.Spatial;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractEditorOperation} for adding a {@link ParticleInfluencer} to the {@link
 * ParticleEmitterNode}.
 *
 * @author JavaSaBr.
 */
public class AddParticleInfluencerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The influencer to add.
     */
    private final ParticleInfluencer influencer;

    /**
     * The index of the parent element.
     */
    private final int index;

    public AddParticleInfluencerOperation(final ParticleInfluencer influencer, final int index) {
        this.influencer = influencer;
        this.index = index;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof ParticleEmitterNode)) return;

            final ParticleEmitterNode emitterNode = (ParticleEmitterNode) parent;
            emitterNode.killAllParticles();
            emitterNode.addInfluencer(influencer);
            emitterNode.emitAllParticles();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(new ParticleInfluencers(emitterNode), influencer, -1));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof ParticleEmitterNode)) return;

            final ParticleEmitterNode emitterNode = (ParticleEmitterNode) parent;
            emitterNode.killAllParticles();
            emitterNode.removeInfluencer(influencer);
            emitterNode.emitAllParticles();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(new ParticleInfluencers(emitterNode), influencer));
        });
    }
}
