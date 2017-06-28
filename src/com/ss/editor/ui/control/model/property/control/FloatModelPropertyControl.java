package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractFloatPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} to edit float values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class FloatModelPropertyControl<T> extends AbstractFloatPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Float model property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public FloatModelPropertyControl(@Nullable final Float element, @NotNull final String paramName,
                                     @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
