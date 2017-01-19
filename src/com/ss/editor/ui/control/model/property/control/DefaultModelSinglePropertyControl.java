package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractDefaultSinglePropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The default implementation of the property control.
 *
 * @author JavaSaBr
 */
public class DefaultModelSinglePropertyControl<T> extends AbstractDefaultSinglePropertyControl<ModelChangeConsumer, T> {

    public DefaultModelSinglePropertyControl(@Nullable final T element, @NotNull final String paramName,
                                             @NotNull final ModelChangeConsumer changeConsumer) {
        super(element, paramName, changeConsumer, newChangeHandler());
    }
}
