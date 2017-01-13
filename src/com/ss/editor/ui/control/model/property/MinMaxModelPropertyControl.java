package com.ss.editor.ui.control.model.property;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link Vector2fModelPropertyControl} for editing min-max value range.
 *
 * @author JavaSaBr.
 */
public class MinMaxModelPropertyControl<T extends Spatial> extends Vector2fModelPropertyControl<T> {

    public MinMaxModelPropertyControl(@NotNull final Vector2f element, @NotNull final String paramName,
                                      @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
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
