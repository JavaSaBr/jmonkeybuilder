package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractBooleanPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} for changing boolean values.
 *
 * @author JavaSaBr
 */
public class BooleanModelPropertyControl<T> extends AbstractBooleanPropertyControl<ModelChangeConsumer, T> {

    public BooleanModelPropertyControl(@NotNull final Boolean element, @NotNull final String paramName,
                                       @NotNull final ModelChangeConsumer changeConsumer) {
        super(element, paramName, changeConsumer, newChangeHandler());
    }
}
