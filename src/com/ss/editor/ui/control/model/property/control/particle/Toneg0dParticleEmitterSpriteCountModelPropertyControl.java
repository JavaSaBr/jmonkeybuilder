package com.ss.editor.ui.control.model.property.control.particle;

import com.jme3.math.Vector2f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.Vector2fModelPropertyControl;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of the {@link Vector2fModelPropertyControl} to edit sprite count of the {@link
 * ParticleEmitterNode}*.
 *
 * @author JavaSaBr.
 */
public class Toneg0dParticleEmitterSpriteCountModelPropertyControl extends
        Vector2fModelPropertyControl<ParticleEmitterNode> {

    /**
     * Instantiates a new Toneg0d particle emitter sprite count model property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public Toneg0dParticleEmitterSpriteCountModelPropertyControl(@NotNull final Vector2f element,
                                                                 @NotNull final String paramName,
                                                                 @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
        getXField().setMinMax(1, Integer.MAX_VALUE);
        getYField().setMinMax(1, Integer.MAX_VALUE);
    }

    @NotNull
    @Override
    protected String getXLabelText() {
        return Messages.MODEL_PROPERTY_COLUMNS + ":";
    }

    @NotNull
    @Override
    protected String getYLabelText() {
        return Messages.MODEL_PROPERTY_ROWS + ":";
    }
}
