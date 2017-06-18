package com.ss.editor.ui.control.property.impl;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import com.ss.rlib.function.SixObjectConsumer;

/**
 * The implementation of the {@link AbstractVector2fPropertyControl} to edit min-max value range.
 *
 * @author JavaSaBr
 */
public abstract class AbstractMinMaxPropertyControl<C extends ChangeConsumer, T extends Spatial>
        extends AbstractVector2fPropertyControl<C, T> {


    public AbstractMinMaxPropertyControl(@Nullable final Vector2f propertyValue, @NotNull final String propertyName,
                                         @NotNull final C changeConsumer, @NotNull
                                         final SixObjectConsumer<C, T, String, Vector2f, Vector2f, BiConsumer<T, Vector2f>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        getXField().setMinMax(0, Integer.MAX_VALUE);
        getYField().setMinMax(0, Integer.MAX_VALUE);
    }

    @Override
    protected float checkResultXValue(final float x, final float y) {
        return min(x, y);
    }

    @Override
    protected float checkResultYValue(final float x, final float y) {
        return max(x, y);
    }

    @NotNull
    @Override
    protected String getXLabelText() {
        return Messages.MODEL_PROPERTY_MIN + ":";
    }

    @NotNull
    @Override
    protected String getYLabelText() {
        return Messages.MODEL_PROPERTY_MAX + ":";
    }
}
