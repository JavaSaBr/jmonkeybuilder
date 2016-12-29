package com.ss.editor.ui.control.model.property.generic;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.AbstractBooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.builder.impl.generic.EditableProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} for changing boolean values.
 *
 * @author JavaSaBr
 */
public class BooleanGenericPropertyControl extends AbstractBooleanModelPropertyControl<EditableProperty<Boolean, ?>> {

    public BooleanGenericPropertyControl(@NotNull final Boolean element, @NotNull final String paramName,
                                         @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void changed(@Nullable final Boolean newValue, @Nullable final Boolean oldValue) {

        final EditableProperty<Boolean, ?> editObject = getEditObject();
        //final ParticleInfluencerPropertyOperation<T, Boolean> operation = new ParticleInfluencerPropertyOperation<>(editObject, parent, getPropertyName(), newValue, oldValue);
        //operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        //modelChangeConsumer.execute(operation);
    }
}
