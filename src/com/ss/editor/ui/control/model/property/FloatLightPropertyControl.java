package com.ss.editor.ui.control.model.property;

import com.jme3.light.Light;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} for editing a number property of the {@link Light}.
 *
 * @author JavaSaBr
 */
public class FloatLightPropertyControl<T extends Light> extends AbstractFloatModelPropertyControl<T> {

    public FloatLightPropertyControl(@NotNull final Float element, @NotNull final String paramName,
                                     @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    @Override
    protected void changed(@Nullable final Float newValue, @Nullable final Float oldValue) {

        final T editObject = getEditObject();

        final LightPropertyOperation<T, Float> operation = new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
