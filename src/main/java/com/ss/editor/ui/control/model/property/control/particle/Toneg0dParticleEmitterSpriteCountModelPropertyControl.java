package com.ss.editor.ui.control.model.property.control.particle;

import com.jme3.math.Vector2f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.Vector2FPropertyControl;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Vector2FPropertyControl} to edit sprite count of the {@link
 * ParticleEmitterNode}*.
 *
 * @author JavaSaBr.
 */
public class Toneg0dParticleEmitterSpriteCountModelPropertyControl extends
        Vector2FPropertyControl<ModelChangeConsumer, ParticleEmitterNode> {

    public Toneg0dParticleEmitterSpriteCountModelPropertyControl(@NotNull final Vector2f element,
                                                                 @NotNull final String paramName,
                                                                 @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
        getXField().setMinMax(1, Integer.MAX_VALUE);
        getYField().setMinMax(1, Integer.MAX_VALUE);
    }
}
