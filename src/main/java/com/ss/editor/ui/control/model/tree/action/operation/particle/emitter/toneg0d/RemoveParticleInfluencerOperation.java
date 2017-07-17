package com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d;

import com.ss.editor.model.node.Toneg0dParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractEditorOperation} to remove a {@link ParticleInfluencer} from a {@link
 * ParticleEmitterNode}*.
 *
 * @author JavaSaBr.
 */
public class RemoveParticleInfluencerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The influencer to remove.
     */
    @NotNull
    private final ParticleInfluencer influencer;

    /**
     * The parent element.
     */
    @NotNull
    private final ParticleEmitterNode emitterNode;

    /**
     * The index of position in the influencers.
     */
    private final int childIndex;

    /**
     * Instantiates a new Remove particle influencer operation.
     *
     * @param influencer  the influencer
     * @param emitterNode the emitter node
     * @param childIndex  the child index
     */
    public RemoveParticleInfluencerOperation(@NotNull final ParticleInfluencer influencer,
                                             @NotNull final ParticleEmitterNode emitterNode, final int childIndex) {
        this.influencer = influencer;
        this.emitterNode = emitterNode;
        this.childIndex = childIndex;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            emitterNode.killAllParticles();
            emitterNode.removeInfluencer(influencer);
            emitterNode.emitAllParticles();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(new Toneg0dParticleInfluencers(emitterNode), influencer));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            emitterNode.killAllParticles();
            emitterNode.addInfluencer(influencer, childIndex);
            emitterNode.emitAllParticles();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(new Toneg0dParticleInfluencers(emitterNode), influencer, childIndex));
        });
    }
}
