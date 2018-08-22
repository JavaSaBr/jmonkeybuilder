package com.ss.editor.model.undo.impl.emitter;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to change a {@link ParticleInfluencer} to a {@link
 * ParticleEmitter}.
 *
 * @author JavaSaBr.
 */
public class ChangeParticleInfluencerOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The particle emitter.
     */
    @NotNull
    private final ParticleEmitter emitter;

    /**
     * The prev influencer.
     */
    @NotNull
    private final ParticleInfluencer prevInfluencer;

    /**
     * The new influencer.
     */
    @NotNull
    private final ParticleInfluencer newInfluencer;

    public ChangeParticleInfluencerOperation(
            @NotNull ParticleInfluencer influencer,
            @NotNull ParticleEmitter emitter
    ) {
        this.prevInfluencer = emitter.getParticleInfluencer();
        this.newInfluencer = influencer;
        this.emitter = emitter;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        emitter.setParticleInfluencer(newInfluencer);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxReplaced(emitter, prevInfluencer, newInfluencer, true, true);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        emitter.setParticleInfluencer(prevInfluencer);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxReplaced(emitter, newInfluencer, prevInfluencer, true, true);
    }
}
