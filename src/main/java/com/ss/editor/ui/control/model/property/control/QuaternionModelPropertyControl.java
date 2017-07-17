package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.math.Quaternion;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractQuaternionPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractQuaternionPropertyControl} to edit {@link Quaternion} values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class QuaternionModelPropertyControl<T> extends AbstractQuaternionPropertyControl<ModelChangeConsumer, T> {

    /**
     * Instantiates a new Quaternion model property control.
     *
     * @param element        the element
     * @param paramName      the param name
     * @param changeConsumer the change consumer
     */
    public QuaternionModelPropertyControl(@NotNull final Quaternion element, @NotNull final String paramName,
                                          @NotNull final ModelChangeConsumer changeConsumer) {
        super(element, paramName, changeConsumer, newChangeHandler());
    }
}
