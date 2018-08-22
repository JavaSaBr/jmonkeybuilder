package com.ss.builder.fx.control.property.impl;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.control.property.PropertyControl;
import com.ss.rlib.fx.control.input.IntegerTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit integer values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the editing object's type.
 * @author JavaSaBr
 */
public class IntegerPropertyControl<C extends ChangeConsumer, D> extends
        NumberPropertyControl<C, D, Integer, IntegerTextField> {

    public IntegerPropertyControl(
            @Nullable Integer propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FromAnyThread
    protected @NotNull IntegerTextField createFieldControl() {
        return new IntegerTextField();
    }
}
