package com.ss.editor.state.editor.impl.particle.emitter;

import com.ss.editor.state.editor.impl.AbstractEditorState;
import com.ss.editor.ui.component.editor.impl.particle.emitter.ParticleEmitterEditor;

import emitter.Emitter;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link AbstractEditorState} for the {@link ParticleEmitterEditor}.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterEditorState extends AbstractEditorState<ParticleEmitterEditor> {

    /**
     * The list of emitters.
     */
    private final Array<Emitter> emitters;

    public ParticleEmitterEditorState(final ParticleEmitterEditor fileEditor) {
        super(fileEditor);

        this.emitters = ArrayFactory.newArray(Emitter.class);
    }
}
