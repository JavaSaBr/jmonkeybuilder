package com.ss.editor.ui.control.app.state.property.control;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.app.state.operation.AppStatePropertyOperation;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.extension.scene.app.state.EditableSceneAppState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import rlib.function.SixObjectConsumer;

/**
 * The base implementation of the property control for the {@link EditableSceneAppState}.
 *
 * @author JavaSaBr
 */
public class AppStatePropertyControl<D, T> extends AbstractPropertyControl<SceneChangeConsumer, D, T> {

    @NotNull
    public static <D, T> SixObjectConsumer<SceneChangeConsumer, D, String, T, T, BiConsumer<D, T>> newChangeHandler() {
        return (changeConsumer, object, propName, newValue, oldValue, handler) -> {

            final AppStatePropertyOperation<D, T> operation = new AppStatePropertyOperation<>(object, propName, newValue, oldValue);
            operation.setApplyHandler(handler);

            changeConsumer.execute(operation);
        };
    }

    public AppStatePropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                   @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
