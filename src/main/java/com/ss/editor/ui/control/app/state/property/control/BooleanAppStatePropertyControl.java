package com.ss.editor.ui.control.app.state.property.control;

import static com.ss.editor.ui.control.app.state.property.control.AppStatePropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractBooleanPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit boolean values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class BooleanAppStatePropertyControl<T> extends AbstractBooleanPropertyControl<SceneChangeConsumer, T> {

    /**
     * Instantiates a new Boolean app state property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public BooleanAppStatePropertyControl(@Nullable final Boolean propertyValue, @NotNull final String propertyName,
                                          @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
