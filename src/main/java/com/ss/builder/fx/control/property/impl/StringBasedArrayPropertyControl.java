package com.ss.builder.ui.control.property.impl;

import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.rlib.fx.control.input.TypedTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit array values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the editing object's type.
 * @param <T> the value's type.
 * @param <F> the field's type.
 * @author JavaSaBr
 */
public class StringBasedArrayPropertyControl<C extends ChangeConsumer, D, T, F extends TypedTextField<T>>
        extends TypedTextFieldPropertyControl<C, D, T, F> {

    public StringBasedArrayPropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public StringBasedArrayPropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, T> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }
}
