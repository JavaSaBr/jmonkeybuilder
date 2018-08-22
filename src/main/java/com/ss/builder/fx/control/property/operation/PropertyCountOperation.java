package com.ss.builder.ui.control.property.operation;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
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

    public PropertyCountOperation(
            @NotNull D target,
            @NotNull String propertyName,
            @Nullable T newValue,
            @Nullable T oldValue
    ) {
        super(target, propertyName, newValue, oldValue);
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull C editor) {
        super.redoInJme(editor);
        apply(target, newValue);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull C editor) {
        super.undoInJme(editor);
        apply(target, oldValue);
    }

    @Override
    @FxThread
    protected void endInFx(@NotNull C editor) {
        super.endInFx(editor);
        editor.notifyFxChangePropertyCount(target);
    }
}