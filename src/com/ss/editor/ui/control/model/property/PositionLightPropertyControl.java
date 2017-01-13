package com.ss.editor.ui.control.model.property;

import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} for editing position's vector of the {@link Light}.
 *
 * @author JavaSaBr
 */
public class PositionLightPropertyControl<T extends Light> extends AbstractVector3fModelPropertyControl<T> {

    public PositionLightPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void changed(@Nullable final Vector3f newValue, @Nullable final Vector3f oldValue) {

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        final T editObject = getEditObject();

        final LightPropertyOperation<T, Vector3f> operation = new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        modelChangeConsumer.execute(operation);
    }
}
