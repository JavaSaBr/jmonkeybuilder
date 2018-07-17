package com.ss.editor.ui.control.property.impl;

import static java.lang.Math.max;
import static java.lang.Math.min;
import com.jme3.math.Vector2f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link Vector2fPropertyControl} to edit min-max value range.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class MinMaxPropertyControl<C extends ChangeConsumer, D> extends Vector2fPropertyControl<C, D> {

    public MinMaxPropertyControl(
            @Nullable Vector2f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        xField.setMinMax(0F, (float) Integer.MAX_VALUE);
        yField.setMinMax(0F, (float) Integer.MAX_VALUE);
    }

    @Override
    @FxThread
    protected float checkResultXValue(float x, float y) {
        return min(x, y);
    }

    @Override
    @FxThread
    protected float checkResultYValue(float x, float y) {
        return max(x, y);
    }
}
