package com.ss.editor.ui.control.property.operation;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyOperation} to edit property count of objects.
 *
 * @param <C> the type of changed consumer
 * @param <D> the type of edited object
 * @param <T> the type of edited property
 * @author JavaSaBr
 */
public class PropertyCountOperation<C extends ChangeConsumer, D, T> extends PropertyOperation<C, D, T> {

    public PropertyCountOperation(@NotNull final D target, @NotNull final String propertyName,
                                  @Nullable final T newValue, @Nullable final T oldValue) {
        super(target, propertyName, newValue, oldValue);
    }

    @Override
    protected void redoInFx(@NotNull final C editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            apply(target, newValue);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangePropertyCount(target));
        });
    }

    @Override
    protected void undoImpl(@NotNull final C editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> {
            apply(target, oldValue);
            EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxChangePropertyCount(target));
        });
    }
}