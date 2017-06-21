package com.ss.editor.ui.control.filter.property.control;

import static com.ss.editor.ui.control.filter.property.control.FilterPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractStringPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit string values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class StringFilterPropertyControl<T> extends AbstractStringPropertyControl<SceneChangeConsumer, T> {

    /**
     * Instantiates a new String filter property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public StringFilterPropertyControl(@Nullable final String propertyValue, @NotNull final String propertyName,
                                       @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
