package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.util.EditorUtil.getAvailableValues;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} to edit the {@link Enum} values.
 *
 * @param <E> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class EnumControlPropertyControl<E extends Enum<?>, T>
        extends EnumModelPropertyControl<T, E> {

    /**
     * Instantiates a new Enum control property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public EnumControlPropertyControl(@NotNull final E element, @NotNull final String paramName,
                                      @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, getAvailableValues(element));
    }
}
