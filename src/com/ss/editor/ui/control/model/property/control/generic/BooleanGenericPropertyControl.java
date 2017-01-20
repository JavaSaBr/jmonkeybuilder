package com.ss.editor.ui.control.model.property.control.generic;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.ModelPropertyControl;
import com.ss.editor.ui.control.model.property.generic.EditableProperty;
import com.ss.editor.ui.control.model.property.operation.EditablePropertyPropertyOperation;
import com.ss.editor.ui.control.property.impl.AbstractBooleanPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} for changing boolean values.
 *
 * @author JavaSaBr
 */
public class BooleanGenericPropertyControl extends AbstractBooleanPropertyControl<ModelChangeConsumer, EditableProperty<Boolean, ?>> {

    public BooleanGenericPropertyControl(@NotNull final Boolean element, @NotNull final String paramName,
                                         @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, null);
    }

    @Override
    protected void changed(@Nullable final Boolean newValue, @Nullable final Boolean oldValue) {

        final EditableProperty<Boolean, ?> editObject = getEditObject();

        final EditablePropertyPropertyOperation<Boolean> operation =
                new EditablePropertyPropertyOperation<>(editObject, newValue, oldValue);

        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
