package com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d;

import com.ss.editor.model.node.Toneg0dParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a {@link ParticleInfluencer} to a {@link
 * ParticleEmitterNode}*.
 *
 * @author JavaSaBr.
 */
public class AddParticleInfluencerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The influencer.
     */
    private final ParticleInfluencer influencer;

    /**
     * The parent.
     */
    private final ParticleEmitterNode parent;

    /**
     * Instantiates a new Add particle influencer operation.
     *
     * @param influencer the influencer
     * @param parent     the parent
     */
    public AddParticleInfluencerOperation(@NotNull final ParticleInfluencer influencer,
                                          @NotNull final ParticleEmitterNode parent) {
        this.influencer = influencer;
        this.parent = parent;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            parent.killAllParticles();
            parent.addInfluencer(influencer);
            parent.emitAllParticles();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(new Toneg0dParticleInfluencers(parent), influencer, -1));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            parent.killAllParticles();
            parent.removeInfluencer(influencer);
            parent.emitAllParticles();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(new Toneg0dParticleInfluencers(parent), influencer));
        });
    }
}
