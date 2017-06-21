package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.light.Light;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractFloatPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} to edit a number property of the {@link Light}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class FloatLightPropertyControl<T extends Light> extends AbstractFloatPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Float light property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public FloatLightPropertyControl(@NotNull final Float element, @NotNull final String paramName,
                                     @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
