package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractDefaultPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The default implementation of the property control.
 *
 * @author JavaSaBr
 */
public class DefaultModelPropertyControl<T> extends AbstractDefaultPropertyControl<ModelChangeConsumer, T> {

    public DefaultModelPropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                       @NotNull final ModelChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
