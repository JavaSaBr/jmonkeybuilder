package com.ss.builder.fx.control.property.impl;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.control.property.PropertyControl;
import com.ss.rlib.fx.control.input.FloatTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit float values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the editing object's type.
 * @author JavaSaBr
 */
public class FloatPropertyControl<C extends ChangeConsumer, D>
        extends NumberPropertyControl<C, D, Float, FloatTextField> {

    public FloatPropertyControl(
            @Nullable Float propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        this(propertyValue, propertyName, changeConsumer, null);
    }

    public FloatPropertyControl(
            @Nullable Float propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, Float> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FromAnyThread
    protected @NotNull FloatTextField createFieldControl() {
        return new FloatTextField();
    }
}
