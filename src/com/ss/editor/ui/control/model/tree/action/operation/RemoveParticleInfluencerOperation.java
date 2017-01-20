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
 * The implementation of the {@link AbstractEditorOperation} for removing a {@link ParticleInfluencer} from the {@link
 * ParticleEmitterNode}.
 *
 * @author JavaSaBr.
 */
public class RemoveParticleInfluencerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The influencer to remove.
     */
    private final ParticleInfluencer influencer;

    /**
     * The index of the parent element.
     */
    private final int index;

    /**
     * The index of position in the influencers.
     */
    private final int childIndex;

    public RemoveParticleInfluencerOperation(final ParticleInfluencer influencer, final int index, final int childIndex) {
        this.influencer = influencer;
        this.index = index;
        this.childIndex = childIndex;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
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

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final Object parent = GeomUtils.getObjectByIndex(currentModel, index);
            if (!(parent instanceof ParticleEmitterNode)) return;

            final ParticleEmitterNode emitterNode = (ParticleEmitterNode) parent;
            emitterNode.killAllParticles();
            emitterNode.addInfluencer(influencer, index);
            emitterNode.emitAllParticles();

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(new ParticleInfluencers(emitterNode), influencer, childIndex));
        });
    }
}
