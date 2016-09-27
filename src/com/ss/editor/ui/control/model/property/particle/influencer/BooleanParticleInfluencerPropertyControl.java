package com.ss.editor.ui.control.model.property.particle.influencer;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.AbstractBooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.AbstractFloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractFloatModelPropertyControl} for editing boolean values in
 * the {@link ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class BooleanParticleInfluencerPropertyControl<T extends ParticleInfluencer> extends AbstractBooleanModelPropertyControl<T> {

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    public BooleanParticleInfluencerPropertyControl(@NotNull final Boolean element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final Object parent) {
        super(element, paramName, modelChangeConsumer);
        this.parent = parent;
    }

    @Override
    protected void changed(@Nullable final Boolean newValue, @Nullable final Boolean oldValue) {

        final T editObject = getEditObject();
        final ParticleInfluencerPropertyOperation<T, Boolean> operation = new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
