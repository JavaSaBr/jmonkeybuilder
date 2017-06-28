package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.math.Vector2f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractVector2fPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractVector2fPropertyControl} to edit {@link Vector2f} values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr.
 */
public class Vector2fModelPropertyControl<T> extends AbstractVector2fPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Vector 2 f model property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public Vector2fModelPropertyControl(@NotNull final Vector2f element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
