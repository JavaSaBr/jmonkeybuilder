package com.ss.editor.ui.control.model.property.control;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.control.property.AbstractPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import rlib.function.SixObjectConsumer;

/**
 * The base implementation of the property control for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelPropertyControl<D, T> extends AbstractPropertyControl<ModelChangeConsumer, D, T> {

    @NotNull
    public static <D, T> SixObjectConsumer<ModelChangeConsumer, D, String, T, T, BiConsumer<D, T>> newChangeHandler() {
        return (changeConsumer, object, propName, newValue, oldValue, handler) -> {

            final ModelPropertyOperation<D, T> operation = new ModelPropertyOperation<>(object, propName, newValue, oldValue);
            operation.setApplyHandler(handler);

            changeConsumer.execute(operation);
        };
    }

    public ModelPropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                @NotNull final ModelChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
