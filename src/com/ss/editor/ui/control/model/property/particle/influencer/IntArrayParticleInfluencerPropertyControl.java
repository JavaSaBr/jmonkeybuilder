package com.ss.editor.ui.control.model.property.particle.influencer;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.AbstractIntArrayModelPropertyControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractIntArrayModelPropertyControl} for editing int array values in the {@link
 * ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class IntArrayParticleInfluencerPropertyControl<T extends ParticleInfluencer> extends AbstractIntArrayModelPropertyControl<T> {

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    public IntArrayParticleInfluencerPropertyControl(@Nullable final int[] element, @NotNull final String paramName,
                                                     @NotNull final ModelChangeConsumer modelChangeConsumer,
                                                     @NotNull final Object parent) {
        super(element, paramName, modelChangeConsumer);
        this.parent = parent;
    }

    @Override
    protected void changed(@Nullable final int[] newValue, @Nullable final int[] oldValue) {

        final T editObject = getEditObject();

        final ParticleInfluencerPropertyOperation<T, int[]> operation =
                new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);

        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
