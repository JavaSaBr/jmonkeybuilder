package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractEnumPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} to edit the {@link Enum} values.
 *
 * @author JavaSaBr
 */
public class EnumModelPropertyControl<T, E extends Enum<?>>
        extends AbstractEnumPropertyControl<ModelChangeConsumer, T, E> {

    public EnumModelPropertyControl(@NotNull final E element, @NotNull final String paramName,
                                    @NotNull final ModelChangeConsumer changeConsumer,
                                    @NotNull final E[] availableValues) {
        super(element, paramName, changeConsumer, availableValues, newChangeHandler());
    }
}
