package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractBooleanPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} for changing boolean values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class BooleanModelPropertyControl<T> extends AbstractBooleanPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Boolean model property control.
     *
     * @param element        the element
     * @param paramName      the param name
     * @param changeConsumer the change consumer
     */
    public BooleanModelPropertyControl(@NotNull final Boolean element, @NotNull final String paramName,
                                       @NotNull final ModelChangeConsumer changeConsumer) {
        super(element, paramName, changeConsumer, newChangeHandler());
    }
}
