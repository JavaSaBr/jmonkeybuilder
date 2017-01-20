package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractMinMaxPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractMinMaxPropertyControl} to edit min-max value range.
 *
 * @author JavaSaBr
 */
public class MinMaxModelPropertyControl<T extends Spatial> extends AbstractMinMaxPropertyControl<ModelChangeConsumer, T> {

    public MinMaxModelPropertyControl(@NotNull final Vector2f element, @NotNull final String paramName,
                                      @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
        getXField().setMinMax(0, Integer.MAX_VALUE);
        getYField().setMinMax(0, Integer.MAX_VALUE);
    }
}
