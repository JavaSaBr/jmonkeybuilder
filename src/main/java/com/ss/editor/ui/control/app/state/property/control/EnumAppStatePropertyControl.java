package com.ss.editor.ui.control.app.state.property.control;

import static com.ss.editor.ui.control.filter.property.control.FilterPropertyControl.newChangeHandler;
import static com.ss.editor.util.EditorUtil.getAvailableValues;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractEnumPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit enum values.
 *
 * @param <E> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class EnumAppStatePropertyControl<E extends Enum<?>, T> extends AbstractEnumPropertyControl<SceneChangeConsumer, T, E> {

    /**
     * Instantiates a new Enum app state property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public EnumAppStatePropertyControl(@NotNull final E propertyValue, @NotNull final String propertyName,
                                       @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, getAvailableValues(propertyValue), newChangeHandler());
    }
}
