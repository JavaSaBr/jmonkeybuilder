package com.ss.editor.ui.control.app.state.property.control;

import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.app.state.operation.AppStatePropertyOperation;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.function.SixObjectConsumer;

import java.util.function.BiConsumer;

/**
 * The base implementation of the property control for the {@link EditableSceneAppState}.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class AppStatePropertyControl<D, T> extends AbstractPropertyControl<SceneChangeConsumer, D, T> {

    /**
     * New change handler six object consumer.
     *
     * @param <D> the type parameter
     * @param <T> the type parameter
     * @return the six object consumer
     */
    @NotNull
    public static <D, T> SixObjectConsumer<SceneChangeConsumer, D, String, T, T, BiConsumer<D, T>> newChangeHandler() {
        return (changeConsumer, object, propName, newValue, oldValue, handler) -> {

            final AppStatePropertyOperation<D, T> operation = new AppStatePropertyOperation<>(object, propName, newValue, oldValue);
            operation.setApplyHandler(handler);

            changeConsumer.execute(operation);
        };
    }

    /**
     * Instantiates a new App state property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public AppStatePropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                   @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
