package com.ss.editor.ui.control.model.property.particle;

import com.jme3.math.Vector2f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.Vector2fModelPropertyControl;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Vector2fModelPropertyControl} for editing sprite count of the {@link
 * ParticleEmitterNode}.
 *
 * @author JavaSaBr.
 */
public class ParticlesSpriteCountModelPropertyControl extends Vector2fModelPropertyControl<ParticleEmitterNode> {

    public ParticlesSpriteCountModelPropertyControl(@NotNull final Vector2f element, @NotNull final String paramName,
                                                    @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
        getXField().setMinMax(1, Integer.MAX_VALUE);
        getYField().setMinMax(1, Integer.MAX_VALUE);
    }

    @NotNull
    @Override
    protected String getXLabelText() {
        return Messages.PARTICLE_EMITTER_COLUMNS + ":";
    }

    @NotNull
    @Override
    protected String getYLabelText() {
        return Messages.PARTICLE_EMITTER_ROWS + ":";
    }
}
