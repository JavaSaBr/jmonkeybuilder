package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractEnumPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} to edit the {@link Enum} values.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @author JavaSaBr
 */
public class EnumModelPropertyControl<T, E extends Enum<?>>
        extends AbstractEnumPropertyControl<ModelChangeConsumer, T, E> {

    /**
     * Instantiates a new Enum model property control.
     *
     * @param element         the element
     * @param paramName       the param name
     * @param changeConsumer  the change consumer
     * @param availableValues the available values
     */
    public EnumModelPropertyControl(@NotNull final E element, @NotNull final String paramName,
                                    @NotNull final ModelChangeConsumer changeConsumer,
                                    @NotNull final E[] availableValues) {
        super(element, paramName, changeConsumer, availableValues, newChangeHandler());
    }
}
