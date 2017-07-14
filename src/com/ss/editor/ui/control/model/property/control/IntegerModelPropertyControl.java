package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractIntegerPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} to edit integer values.
 *
 * @param <T> the type of edited object
 * @author JavaSaBr
 */
public class IntegerModelPropertyControl<T> extends AbstractIntegerPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Integer model property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public IntegerModelPropertyControl(@Nullable final Integer element, @NotNull final String paramName,
                                       @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
