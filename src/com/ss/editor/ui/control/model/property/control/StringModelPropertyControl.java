package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractStringPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit string values.
 *
 * @author JavaSaBr
 */
public class StringModelPropertyControl<T> extends AbstractStringPropertyControl<ModelChangeConsumer, T> {

    public StringModelPropertyControl(@Nullable final String propertyValue, @NotNull final String propertyName,
                                      @NotNull final ModelChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
