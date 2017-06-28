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
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class MinMaxModelPropertyControl<T extends Spatial> extends AbstractMinMaxPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Min max model property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public MinMaxModelPropertyControl(@NotNull final Vector2f element, @NotNull final String paramName,
                                      @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
        getXField().setMinMax(0, Integer.MAX_VALUE);
        getYField().setMinMax(0, Integer.MAX_VALUE);
    }
}
