package com.ss.editor.ui.control.property.impl;

import static java.lang.Math.max;
import static java.lang.Math.min;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link Vector2FPropertyControl} to edit min-max value range.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public class MinMaxPropertyControl<C extends ChangeConsumer, T extends Spatial> extends Vector2FPropertyControl<C, T> {

    public MinMaxPropertyControl(
            @Nullable Vector2f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        getXField().setMinMax(0, Integer.MAX_VALUE);
        getYField().setMinMax(0, Integer.MAX_VALUE);
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
