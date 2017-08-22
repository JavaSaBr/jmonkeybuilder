package com.ss.editor.ui.control.model.property.operation;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.ui.control.property.operation.PropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractEditorOperation} to edit property count of models in the {@link
 * ModelFileEditor}*.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class ModelPropertyCountOperation<D, T> extends PropertyOperation<ModelChangeConsumer, D, T> {
    public ModelPropertyCountOperation(@NotNull final D target, @NotNull final String propertyName, @Nullable final T newValue,
                                       @Nullable final T oldValue) {
        super(target, propertyName, newValue, oldValue);
    }
}