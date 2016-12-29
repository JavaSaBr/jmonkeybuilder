package com.ss.editor.ui.control.model.property;

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
    }

    @Override
    protected float checkResultValue(final float original) {
        return Math.max(original, 0);
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
