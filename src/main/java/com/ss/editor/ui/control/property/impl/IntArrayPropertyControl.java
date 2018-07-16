package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.rlib.fx.control.input.IntegerArrayTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit int array values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the editing object's type.
 * @author JavaSaBr
 */
public class IntArrayPropertyControl<C extends ChangeConsumer, D>
        extends StringBasedArrayPropertyControl<C, D, int[], IntegerArrayTextField> {

    public IntArrayPropertyControl(
            @Nullable int[] propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public IntArrayPropertyControl(
            @Nullable int[] propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, int[]> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FromAnyThread
    protected @NotNull IntegerArrayTextField createFieldControl() {
        return new IntegerArrayTextField();
    }
}
