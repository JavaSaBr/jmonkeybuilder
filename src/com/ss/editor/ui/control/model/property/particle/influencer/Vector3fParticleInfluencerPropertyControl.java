package com.ss.editor.ui.control.model.property.particle.influencer;

import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.AbstractFloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.AbstractVector3fModelPropertyControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link AbstractFloatModelPropertyControl} for editing vector3f values in the {@link
 * ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public class Vector3fParticleInfluencerPropertyControl<T extends ParticleInfluencer> extends AbstractVector3fModelPropertyControl<T> {

    /**
     * The parent of the influencer.
     */
    @NotNull
    private Object parent;

    public Vector3fParticleInfluencerPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName,
                                                     @NotNull final ModelChangeConsumer modelChangeConsumer,
                                                     @NotNull final Object parent) {
        super(element, paramName, modelChangeConsumer);
        this.parent = parent;
    }

    @Override
    protected void changed(@Nullable final Vector3f newValue, @Nullable final Vector3f oldValue) {

        final T editObject = getEditObject();

        final ParticleInfluencerPropertyOperation<T, Vector3f> operation =
                new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);

        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
