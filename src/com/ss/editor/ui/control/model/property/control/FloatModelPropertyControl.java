package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractFloatPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} to edit float values.
 *
 * @author JavaSaBr
 */
public class FloatModelPropertyControl<T> extends AbstractFloatPropertyControl<ModelChangeConsumer, T> {

    public FloatModelPropertyControl(@Nullable final Float element, @NotNull final String paramName,
                                     @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
