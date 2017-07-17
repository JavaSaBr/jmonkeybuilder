package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractVector3fPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractVector3fPropertyControl} to edot vector3f values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class Vector3fModelPropertyControl<T> extends AbstractVector3fPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Vector 3 f model property control.
     *
     * @param element             the element
     * @param paramName           the param name
     * @param modelChangeConsumer the model change consumer
     */
    public Vector3fModelPropertyControl(@Nullable final Vector3f element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
