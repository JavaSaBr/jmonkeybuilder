package com.ss.editor.ui.control.model.property.particle.influencer;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.AbstractEnumModelPropertyControl;
import com.ss.editor.ui.control.model.property.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link Enum} values.
 *
 * @author JavaSaBr
 */
public class EnumParticleInfluencerEmitterPropertyControl<T extends ParticleInfluencer, E extends Enum<E>> extends AbstractEnumModelPropertyControl<T, E> {

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    public EnumParticleInfluencerEmitterPropertyControl(@NotNull final E element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final E[] availableValues, final @NotNull Object parent) {
        super(element, paramName, modelChangeConsumer, availableValues);
        this.parent = parent;
    }

    @Override
    protected void changed(@Nullable final E newValue, @Nullable final E oldValue) {

        final T editObject = getEditObject();
        final ParticleInfluencerPropertyOperation<T, E> operation = new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
